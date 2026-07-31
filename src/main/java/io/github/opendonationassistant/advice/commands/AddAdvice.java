package io.github.opendonationassistant.advice.commands;

import io.github.opendonationassistant.advice.repository.AdviceRepository;
import io.github.opendonationassistant.advice.view.AdviceDto;
import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Inject;

@Controller
public class AddAdvice extends BaseController {

  private final AdviceRepository adviceRepository;

  @Inject
  public AddAdvice(AdviceRepository adviceRepository) {
    this.adviceRepository = adviceRepository;
  }

  @Post("/advice/commands/add-advice")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public HttpResponse<AdviceDto> addAdvice(
    Authentication auth,
    @Body AddAdviceCommand command
  ) {
    var ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    return HttpResponse.ok(adviceRepository.create(command.text()).asDto());
  }

  @Serdeable
  public static record AddAdviceCommand(String text) {}
}
