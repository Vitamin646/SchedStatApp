package io.github.vitamin646dev.schedstatapp.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ViewTypeTest {

	@Test
	void everyViewHasFxmlResourceDeclaringItsController() throws IOException {
		for (ViewType type : ViewType.values()) {
			try (InputStream in = ViewType.class.getResourceAsStream(type.getFxmlPath())) {
				assertNotNull(in, "Missing FXML for " + type + ": " + type.getFxmlPath());
				String fxml = new String(in.readAllBytes(), StandardCharsets.UTF_8);
				assertTrue(fxml.contains("fx:controller=\"" + type.getControllerClass().getName() + "\""),
						type + " FXML does not declare " + type.getControllerClass().getName());
			}
		}
	}
}
