package io.github.opendonationassistant.guides.view;

import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.github.opendonationassistant.guides.repository.GuidesRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;

@Controller
public class GetReadGuides extends BaseController {

  private final GuidesRepository guidesRepository;

  @Inject
  public GetReadGuides(GuidesRepository guidesRepository) {
    this.guidesRepository = guidesRepository;
  }

  @Get("/guides/commands/get-read")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public HttpResponse<GuidesDto> getReadGuides(Authentication auth) {
    var ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    return HttpResponse.ok(guidesRepository.get(ownerId.get()).asDto());
  }
}
