package com.faraway.auditall.service;

import com.faraway.auditall.entity.CheckPhoto;

import java.util.List;

public interface CheckPhotoService {

    public List<CheckPhoto> findAllCheckPhoto();
    public int insertOrUpdateCheckPhoto(CheckPhoto checkPhoto);
    public void deleteAllCheckPhoto();
}
