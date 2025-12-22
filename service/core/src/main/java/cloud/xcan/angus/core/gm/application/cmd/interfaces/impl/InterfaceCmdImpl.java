package cloud.xcan.angus.core.gm.application.cmd.interfaces.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.interfaces.InterfaceCmd;
import cloud.xcan.angus.core.gm.application.query.interfaces.InterfaceQuery;
import cloud.xcan.angus.core.gm.domain.interfaces.Interface;
import cloud.xcan.angus.core.gm.domain.interfaces.InterfaceRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of interface command service
 */
@Biz
public class InterfaceCmdImpl extends CommCmd<Interface, Long> implements InterfaceCmd {

  @Resource
  private InterfaceRepo interfaceRepo;

  @Resource
  private InterfaceQuery interfaceQuery;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Interface deprecate(Long id, Boolean deprecated, String deprecationNote) {
    return new BizTemplate<Interface>() {
      Interface interfaceDb;

      @Override
      protected void checkParams() {
        interfaceDb = interfaceQuery.findAndCheck(id);
      }

      @Override
      protected Interface process() {
        interfaceDb.setDeprecated(deprecated);
        interfaceDb.setDeprecationNote(deprecationNote);
        interfaceRepo.save(interfaceDb);
        return interfaceDb;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Interface syncFromService(String serviceName) {
    // TODO: Implement sync from service
    // This would typically involve:
    // 1. Fetching OpenAPI/Swagger spec from service
    // 2. Parsing the spec
    // 3. Creating/updating interfaces
    // For now, return null as placeholder
    return null;
  }

  @Override
  protected BaseRepository<Interface, Long> getRepository() {
    return interfaceRepo;
  }
}

