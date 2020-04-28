package com.axelor.apps.gsuite.service.batch;

import com.axelor.apps.base.service.administration.AbstractBatch;
import com.axelor.apps.gsuite.db.GoogleAccount;
import com.axelor.apps.gsuite.service.GSuiteAOSContactService;
import com.axelor.auth.db.User;
import com.axelor.auth.db.repo.UserRepository;
import com.axelor.exception.AxelorException;
import com.axelor.exception.service.TraceBackService;
import com.google.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BatchGSuiteContactService extends AbstractBatch {

  @Inject protected UserRepository userRepo;
  @Inject protected GSuiteAOSContactService gSuiteAOSContactService;

  @Override
  protected void process() {
    List<User> users = userRepo.all().filter("self.googleAccount != null").fetch();
    Set<GoogleAccount> accountSet =
        users.stream().map(User::getGoogleAccount).collect(Collectors.toSet());
    for (GoogleAccount account : accountSet) {
      try {
        gSuiteAOSContactService.sync(account);
        incrementDone();
      } catch (AxelorException e) {
        TraceBackService.trace(e, "", batch.getId());
        incrementAnomaly();
      }
    }
  }
}
