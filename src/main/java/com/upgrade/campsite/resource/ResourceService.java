package com.upgrade.campsite.resource;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResourceService {

  private ResourceRepository resourceRepository;

  @Autowired
  public ResourceService(ResourceRepository resourceRepository) {
    super();
    this.resourceRepository = resourceRepository;
  }

  public Resource findById(String id) {
    Optional<Resource> record = resourceRepository.findById(id);

    return record.isPresent() ? record.get() : null;
  }
  
  public List<Resource> findAll() {
    return resourceRepository.findAll();
  }

}
