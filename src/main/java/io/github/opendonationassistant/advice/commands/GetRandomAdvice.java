package io.github.opendonationassistant.advice.commands;

import io.github.opendonationassistant.advice.repository.AdviceRepository;
import io.github.opendonationassistant.advice.view.AdviceDto;
import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;

@Controller
public class GetRandomAdvice extends BaseController {

  private final AdviceRepository adviceRepository;

  @Inject
  public GetRandomAdvice(AdviceRepository adviceRepository) {
    this.adviceRepository = adviceRepository;
  }

  @Get("/advice/commands/get-random-advice")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public HttpResponse<AdviceDto> getRandomAdvice(Authentication auth) {
    var ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    return adviceRepository
      .getRandom()
      .map(advice -> HttpResponse.<AdviceDto>ok(advice.asDto()))
      .orElseGet(HttpResponse::notFound);
  }
}
