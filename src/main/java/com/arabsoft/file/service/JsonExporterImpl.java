package com.arabsoft.file.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.arabsoft.file.model.File;
import com.google.gson.Gson;

@Service
public class JsonExporterImpl implements JsonExporter {

	@Override
	public String export(List<File> files) {
		Gson gson = new Gson();
		String customerInJson = gson.toJson(files);
		return customerInJson;
	}

}
