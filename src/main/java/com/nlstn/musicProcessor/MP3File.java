package com.nlstn.musicProcessor;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;

public class MP3File {

	static {

	}

	private static final String	outputPath	= "D:\\Media\\Music\\";

	private File				file;
	private Mp3File				mp3File;

	private ID3v1				id3Tag;

	private String				newLoc;

	public MP3File(File file) {
		this.file = file;
	}

	public boolean loadMp3Data() {
		try {
			mp3File = new Mp3File(file);
		}
		catch (UnsupportedTagException | InvalidDataException | IOException e) {
			System.err.println("Unable to load Mp3 data " + file.getAbsolutePath());
			return false;
		}
		return getId3Tags();
	}

	public boolean isOfType(List<String> types) {
		return types.contains(getExtension().toLowerCase());
	}

	public boolean deleteIfOfType(List<String> types) {
		if (types.contains(getExtension().toLowerCase())) {
			if (!file.delete()) {
				System.out.println("Failed to delete file " + file.getAbsolutePath());
			}
			else {
				System.out.println("Deleting file " + file.getAbsolutePath());
			}
			return true;
		}
		return false;
	}

	public void moveTonewLoc() {
		try {
			File newFile = new File(getNewLoc());
			newFile.getParentFile().mkdirs();
			newFile.createNewFile();
		}
		catch (IOException e1) {
			System.err.println("Failed to get newFile " + newLoc + ", " + e1.getClass().getName() + ": " + e1.getMessage());
			return;
		}
		try {
			mp3File.save(newLoc);
			file.delete();
		}
		catch (IOException | NotSupportedException e) {
			System.err.println("Failed to move " + file.getAbsolutePath() + " to " + newLoc + ", " + e.getClass().getName());
		}
	}

	public String getNewLoc() {
		return newLoc = ((outputPath + (id3Tag.getArtist().trim() + " - " + id3Tag.getAlbum().trim()).replace(":", "") + "\\").replace("?", "").replaceAll("ue", "�").replaceAll("/", "").replace("'", "") + id3Tag.getTitle().replaceAll("[\\\\/:*?\"<>|]", "") + getExtension()).trim();
	}

	private boolean getId3Tags() {
		id3Tag = null;
		if (mp3File.hasId3v1Tag()) {
			id3Tag = mp3File.getId3v1Tag();
			if ((id3Tag.getAlbum() == "" || id3Tag.getArtist() == "" || id3Tag.getTitle() == "" || id3Tag.getTrack() == "") && mp3File.hasId3v2Tag()) {
				id3Tag = mp3File.getId3v2Tag();
			}
		}
		else
			if (mp3File.hasId3v2Tag()) {
				id3Tag = mp3File.getId3v2Tag();
			}
			else {
				System.err.println("Missing ID3Tags " + file.getAbsolutePath());
				return false;
			}
		if (id3Tag.getArtist() == null) {
			try {
				id3Tag.setArtist(((ID3v2) id3Tag).getAlbumArtist());
			}
			catch (Exception e) {

			}
		}
		if (id3Tag.getAlbum() == null || id3Tag.getAlbum() == "" || id3Tag.getArtist() == null || id3Tag.getArtist() == "" || id3Tag.getTitle() == null || id3Tag.getTitle() == "" || id3Tag.getTrack() == null || id3Tag.getTrack() == "") {
			System.err.println("Missing ID3Tags " + file.getAbsolutePath());
			return false;
		}
		return true;
	}

	private String getExtension() {
		return file.getName().substring(file.getName().lastIndexOf('.'));
	}

}