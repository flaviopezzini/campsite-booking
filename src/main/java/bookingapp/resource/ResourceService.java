package bookingapp.resource;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import bookingapp.shared.IncorrectResourceSetupException;

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
  
  /**
   * Returns the only resource we have so far.
   * If we add more resources, we'll have to start forcing the clients to provide one
   * @return
   */
  public Resource getOrDefault(String resourceId) {
    if (StringUtils.isEmpty(resourceId)) {
      List<Resource> resources = resourceRepository.findAll();
      if (resources.size() != 1) {
        throw new IncorrectResourceSetupException(ResourceErrorMessage.INCORRECT_RESOURCE_SETUP.message());
      }
      return resources.get(0);
    } else {
      return findById(resourceId);
    }
  }

}
