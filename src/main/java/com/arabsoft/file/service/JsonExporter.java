package com.arabsoft.file.service;

import java.util.List;

import com.arabsoft.file.model.File;

public interface JsonExporter {
	
	String export(List<File> customers);
}
