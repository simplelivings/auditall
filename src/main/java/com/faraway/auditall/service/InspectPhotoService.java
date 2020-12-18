package com.faraway.auditall.service;

import com.faraway.auditall.entity.InspectInfo;
import com.faraway.auditall.entity.InspectPhoto;

public interface InspectPhotoService {

    public int insertOrUpdateInspectPhoto(InspectPhoto inspectPhoto);
    public void deleteAllInspectPhoto();

}
