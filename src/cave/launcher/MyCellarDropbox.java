package Cave.launcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import com.dropbox.core.DbxAccountInfo;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2016</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.1
 * @since 13/04/16
 */

public class MyCellarDropbox {

	private final DbxClient dbxClient;

	public MyCellarDropbox()
			throws IOException, DbxException {
		DbxRequestConfig dbxRequestConfig = new DbxRequestConfig(
				"MyCellar/1.0", Locale.getDefault().toString());
		dbxClient = new DbxClient(dbxRequestConfig, "97ZseJMv-CwAAAAAAAAeilUCOLiZvEi-Op3WW_C7gfDahVD3MGi2O5_yVSxnoMjw");
	}

	/* returns Dropbox size in GB */
	public long getDropboxSize() throws DbxException {
		long dropboxSize = 0;
		DbxAccountInfo dbxAccountInfo = dbxClient.getAccountInfo();
		// in GB :)
		dropboxSize = dbxAccountInfo.quota.total / 1024 / 1024 / 1024;
		return dropboxSize;
	}

	/*public void uploadToDropbox(String fileName) throws DbxException,
			IOException {
		File inputFile = new File(fileName);
		FileInputStream fis = new FileInputStream(inputFile);
		try {
			DbxEntry.File uploadedFile = dbxClient.uploadFile("/" + fileName,
					DbxWriteMode.add(), inputFile.length(), fis);
			String sharedUrl = dbxClient.createShareableUrl("/" + fileName);
			System.out.println("Uploaded: " + uploadedFile.toString() + " URL "
					+ sharedUrl);
		} finally {
			fis.close();
		}
	}

	public void createFolder(String folderName) throws DbxException {
		dbxClient.createFolder("/" + folderName);
	}*/

	public DbxEntry.WithChildren listDropboxFolders(String folderPath) throws DbxException {
		DbxEntry.WithChildren listing = dbxClient.getMetadataWithChildren("/" + folderPath);

		return listing;
	}

	public void downloadFromDropbox(String fileName, File out) throws DbxException,
	IOException {
		FileOutputStream outputStream = new FileOutputStream(out);
		try {
			dbxClient.getFile("/" + fileName, null, outputStream);
		} finally {
			outputStream.close();
		}
	}

	public void downloadFolderFromDropbox(String folder, String destination) throws DbxException,
	IOException {
		DbxEntry.WithChildren list = listDropboxFolders(folder);
		for (DbxEntry child : list.children) {
			File f = new File(new File(destination, folder), child.name);
			FileOutputStream outputStream = new FileOutputStream(f);
			try {
				dbxClient.getFile("/" + folder + "/" + child.name, null, outputStream);
			} finally {
				outputStream.close();
			}
		}
	}

}
