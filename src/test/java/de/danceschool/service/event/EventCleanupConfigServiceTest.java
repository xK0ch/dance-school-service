package de.danceschool.service.event;

import de.danceschool.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventCleanupConfigServiceTest {

    @Mock
    private EventCleanupConfigRepository repository;

    @InjectMocks
    private EventCleanupConfigService service;

    @Test
    void getConfig_returnsExistingConfig() {
        EventCleanupConfig existing = new EventCleanupConfig(false);
        when(repository.findAll()).thenReturn(List.of(existing));

        EventCleanupConfigResponse result = service.getConfig();

        assertThat(result.enabled()).isFalse();
    }

    @Test
    void getConfig_whenMissing_throwsResourceNotFound() {
        // The single config row is seeded by Flyway migration V11. If it ever goes
        // missing, we surface a 404 instead of silently inserting from a read-only transaction.
        when(repository.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> service.getConfig())
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateConfig_togglesEnabled() {
        EventCleanupConfig existing = new EventCleanupConfig(true);
        when(repository.findAll()).thenReturn(List.of(existing));
        when(repository.save(any(EventCleanupConfig.class))).thenAnswer(i -> i.getArgument(0));

        EventCleanupConfigResponse result = service.updateConfig(new EventCleanupConfigRequest(false));

        assertThat(result.enabled()).isFalse();
        ArgumentCaptor<EventCleanupConfig> captor = ArgumentCaptor.forClass(EventCleanupConfig.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().isEnabled()).isFalse();
    }

    @Test
    void isEnabled_defaultsToTrueWhenMissing() {
        when(repository.findAll()).thenReturn(List.of());

        assertThat(service.isEnabled()).isTrue();
    }
}
