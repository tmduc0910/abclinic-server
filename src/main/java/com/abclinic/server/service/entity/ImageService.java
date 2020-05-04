package com.abclinic.server.service.entity;

import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.entity.Image;
import com.abclinic.server.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * @author tmduc
 * @package com.abclinic.server.service.entity
 * @created 5/4/2020 10:27 AM
 */
@Service
public class ImageService implements DataMapperService<Image> {
    private ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    @Transactional
    public Image getById(long id) throws NotFoundException {
        return imageRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public Image save(Image obj) {
        return imageRepository.save(obj);
    }
}
