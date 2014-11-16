package org.codeforafrica.ziwaphi;

import java.util.ArrayList;

import org.codeforafrica.ziwaphi.model.Media;
import org.codeforafrica.ziwaphi.model.Project;

//used to delete a project's media on storage
public class ProjectCleaner {
	
	public static void clean (Project project)
	{
		// FIXME default to use first scene
		ArrayList<Media> alMedia = project.getScenesAsArray()[0].getMediaAsList();
		
	}

}
