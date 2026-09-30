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
import io.micronaut.validation.Validated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@Controller
@Validated
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
    @Valid @Body AddAdviceCommand command
  ) {
    var ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    return HttpResponse.ok(AdviceDto.from(adviceRepository.create(command.text()).data()));
  }

  @Serdeable
  public static record AddAdviceCommand(@NotBlank String text) {}
}
