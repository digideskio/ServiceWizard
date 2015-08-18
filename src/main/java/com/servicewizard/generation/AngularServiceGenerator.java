
package com.servicewizard.generation;

import com.servicewizard.model.Service;
import com.servicewizard.model.ServiceMethod;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class AngularServiceGenerator {

	public void generate(String moduleName, String urlBase, Service service) {
		generate(moduleName, urlBase, service, System.out);
	}

	public void generate(String moduleName, String urlBase, Service service, String fileName) {
		try {
			generate(moduleName, urlBase, service, new PrintStream(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void generate(String moduleName, String urlBase, Service service, PrintStream output) {
		indentation = 0;
		whitespace = "";

		output.println(String.format("%sangular.module('%s')", whitespace, moduleName));
		output.println(String.format("%s.factory('%s', ['$http', function($http) {", whitespace, service.getName()));

		indent();
		output.println(String.format("%svar urlBase = '%s';", whitespace, urlBase));
		output.println(String.format("%sreturn {", whitespace));

		indent();
		for (ServiceMethod method : service.getMethods()) {
			output.println();

			// Documentation block
			addDocumentationBlock(method, output);

			// Method parameters
			boolean hasQueryParameters = !method.getQueryParameters().isEmpty();
			boolean hasRequestBody = method.hasRequestBody();
			if (hasRequestBody && hasQueryParameters)
				output.println(String.format("%s%s: function(data, params) {", whitespace, method.getName()));
			else if (hasRequestBody)
				output.println(String.format("%s%s: function(data) {", whitespace, method.getName()));
			else if (hasQueryParameters)
				output.println(String.format("%s%s: function(params) {", whitespace, method.getName()));
			else
				output.println(String.format("%s%s: function() {", whitespace, method.getName()));

			// Request object
			indent();
			output.println(String.format("%svar request = {", whitespace));

			indent();
			output.println(String.format("%surl: urlBase + '%s',", whitespace, method.getRelativePath()));
			output.println(String.format("%smethod: '%s',", whitespace, method.getVerb()));

			if (hasRequestBody)
				output.println(String.format("%sdata: data,", whitespace));

			if (hasQueryParameters)
				output.println(String.format("%sparams: params,", whitespace));

			unindent();
			output.println(String.format("%s};", whitespace));
			output.println(String.format("%sreturn $http(request);", whitespace));

			unindent();
			output.println(String.format("%s},", whitespace));
		}
		unindent();
		output.println(String.format("%s}", whitespace));
		unindent();
		output.println(String.format("%s}]);", whitespace));
	}

	public void addDocumentationBlock(ServiceMethod method, PrintStream output) {
		output.println(String.format("%s/**", whitespace));

		// Title
		if (method.getTitle() != null)
			output.println(String.format("%s * %s", whitespace, method.getTitle()));

		output.println(String.format("%s *", whitespace));

		// Description
		if (method.getDescription() != null)
			output.println(String.format("%s * %s", whitespace, method.getDescription()));

		// Parameters
		if (!method.getQueryParameters().isEmpty()) {
			output.println(String.format("%s * Params:", whitespace));
			for (String parameter : method.getQueryParameters())
				output.println(String.format("%s *   %s", whitespace, parameter));
		}

		output.println(String.format("%s*/", whitespace));
	}

	private void indent() {
		++indentation;
		whitespace += "    ";
	}

	private void unindent() {
		--indentation;
		whitespace = "";
		for (int i = 0; i<indentation; ++i)
			whitespace += "    ";
	}

	private int indentation;
	private String whitespace;
}
