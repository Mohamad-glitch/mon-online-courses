package com.example.mononlinecourses.service;

import com.example.mononlinecourses.dto.CreateTagRequest;
import com.example.mononlinecourses.dto.ShowTagsResponse;
import com.example.mononlinecourses.exception.TagNameCantBeEmpty;
import com.example.mononlinecourses.mapper.TagMapper;
import com.example.mononlinecourses.model.Tag;
import com.example.mononlinecourses.repository.TagDao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {

    private final TagDao tagDao;


    public TagService(TagDao tagDao) {
        this.tagDao = tagDao;
    }


    public String tagNameTrimAndLowerCase(CreateTagRequest createTagRequest) {
        String temp = createTagRequest.tagName().toLowerCase().trim();
        if (temp.isEmpty())
            throw new TagNameCantBeEmpty("tag name cant be empty");
        return temp;
    }

    public boolean checkIfTagNameIsInDB(Tag tag) {
        return tagDao.existsTagByName(tag.getName());
    }

    public List<ShowTagsResponse> showAllTags() {
        return tagDao.findAll().stream().map(TagMapper::fromTagToShowTagsResponse).toList();
    }


    public void saveTag(CreateTagRequest tagName) {

        Tag tag = new Tag();
        tag.setName(tagNameTrimAndLowerCase(tagName));

        if (!checkIfTagNameIsInDB(tag)) {
            tagDao.save(tag);
        }
    }


    public void saveTags(List<CreateTagRequest> createTagRequest) {
        for (CreateTagRequest newTag : createTagRequest) {
            Tag tag = new Tag();
            tag.setName(tagNameTrimAndLowerCase(newTag));

            if (!checkIfTagNameIsInDB(tag)) {
                tagDao.save(tag);
            }
        }
    }

    public List<Tag> getAddedTags(List<String> tags) {
        return tags.stream().map(tagDao::findTagsByName).toList();
    }
}
