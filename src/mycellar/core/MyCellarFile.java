package mycellar.core;

import mycellar.MyCellarUtils;
import mycellar.Program;
import mycellar.core.datas.MyCellarBottleContenance;
import mycellar.core.exceptions.UnableToOpenFileException;
import mycellar.core.exceptions.UnableToOpenMyCellarFileException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static mycellar.Program.getWorkDir;
import static mycellar.ProgramConstants.UNTITLED1_SINFO;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2020
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.6
 * @since 24/10/22
 */
public class MyCellarFile {

  private final MyLinkedHashMap caveConfig = new MyLinkedHashMap();
  private File file;
  private boolean newFile = false;

  public MyCellarFile() {
    file = new File(getWorkDir(true) + UNTITLED1_SINFO);
  }

  public MyCellarFile(String file) {
    this.file = new File(file);
  }

  public void setNewFile(boolean newFile) {
    this.newFile = newFile;
  }

  public boolean isNewFile() {
    return newFile;
  }

  private static void Debug(String sText) {
    Program.Debug("MyCellarFile: " + sText);
  }

  /**
   * zipDir: Compression de repertoire
   *
   * @param fileName File
   * @return boolean
   */
  private static void zipDir(File fileName) {

    final String workDir = getWorkDir(false);
    Debug("zipDir: Zipping in " + workDir + " with archive " + fileName);
    try {
      // creation d'un flux d'ecriture sur fichier
      var dest = new FileOutputStream(fileName);
      // calcul du checksum : Adler32 (plus rapide) ou CRC32
      var checksum = new CheckedOutputStream(dest, new Adler32());
      // creation d'un buffer d'ecriture
      var buff = new BufferedOutputStream(checksum);
      // creation d'un flux d'ecriture Zip
      try (var out = new ZipOutputStream(buff)) {
        // specification de la methode de compression
        out.setMethod(ZipOutputStream.DEFLATED);
        // specifier la qualite de la compression 0..9
        out.setLevel(Deflater.BEST_COMPRESSION);

        // extraction de la liste des fichiers du repertoire courant
        File f = new File(workDir);
        String[] files = f.list();
        // pour chacun des fichiers de la liste
        if (files != null) {
          LinkedList<String> zipEntryList = new LinkedList<>();
          int BUFFER = 2048;
          for (String file : files) {
            final String workDir1 = getWorkDir(true);
            f = new File(workDir1 + file);
            if (f.isDirectory() || UNTITLED1_SINFO.compareTo(file) == 0) {
              continue;
            }
            // creation d'un flux de lecture
            var inputStream = new FileInputStream(workDir1 + file);
            // creation d'un tampon de lecture sur ce flux
            try (var bufferedInputStream = new BufferedInputStream(inputStream, BUFFER)) {
              // creation d'en entree Zip pour ce fichier
              String name = MyCellarUtils.removeAccents(file);
              var entry = new ZipEntry(name);
              if (zipEntryList.contains(name)) {
                continue;
              }
              zipEntryList.add(name);
              // ajout de cette entree dans le flux d'ecriture de l'archive Zip
              out.putNextEntry(entry);
              // ecriture du fichier par paquet de BUFFER octets dans le flux d'ecriture
              int count;
              // buffer temporaire des donnees a ecrire dans le flux de sortie
              byte[] data = new byte[BUFFER];
              while ((count = bufferedInputStream.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
              }
              // Close the current entry
              out.closeEntry();
            }
            inputStream.close();
          }
        }
      }
      buff.close();
      checksum.close();
      dest.close();
    } catch (IOException e) {
      Debug("zipDir: Error while zipping");
      Program.showException(e, false);
    }
    Debug("zipDir OK");
  }

  public void unzip() throws UnableToOpenFileException {
    if (!file.exists()) {
      throw new UnableToOpenMyCellarFileException("File doesn't exist: " + file.getAbsolutePath());
    }
    try {
      // Dezippage
      final String workDir = getWorkDir(false);
      boolean unzipOK = unzipDir(workDir);
      final String absolutePath = file.getAbsolutePath();
      Debug("Unzipping " + absolutePath + " to " + workDir + (unzipOK ? " OK" : " KO"));
      if (!unzipOK) {
        throw new UnableToOpenFileException("Unzipping error for file: " + absolutePath);
      }
    } catch (UnableToOpenFileException e) {
      Debug("ERROR: Unable to unzip file " + file.getAbsolutePath());
      Program.showException(e, false);
      throw new UnableToOpenFileException("Unzipping error: " + e.getMessage());
    }
  }

  public boolean exists() {
    return file.exists() && file.canWrite();
  }

  public boolean isFileSavable() {
    return !file.getName().endsWith(UNTITLED1_SINFO) && exists();
  }

  public void save() {
    saveAs(file);
  }

  public void saveAs(File newfile) {
    saveCaveProperties();
    zipDir(newfile);
    file = newfile;
  }

  /**
   * Save Properties for current cave
   */
  private void saveCaveProperties() {
    MyCellarBottleContenance.save();
    Program.saveProperties(caveConfig, Program.getConfigFilePath());
  }

  public File getFile() {
    return file;
  }

  public MyLinkedHashMap getCaveConfig() {
    return caveConfig;
  }

  /**
   * unzipDir: Unzip the archive in a directory
   *
   * @param targetDir String
   * @return boolean
   */
  private boolean unzipDir(String targetDir) {
    try {
      String fileName = file.getAbsolutePath();
      Debug("Unzip: Archive " + fileName);

      try (var fileInputStream = new FileInputStream(file);
           var bufferedInputStream = new BufferedInputStream(fileInputStream);
           var zipInputStream = new ZipInputStream(bufferedInputStream)) {
        ZipEntry entry;
        int buffer = 2048;
        while ((entry = zipInputStream.getNextEntry()) != null) {
          File f = new File(targetDir);
          if (f.exists() || f.mkdir()) {
            var fileOutputStream = new FileOutputStream(targetDir + File.separator + entry.getName());
            Debug("Unzip: File " + targetDir + File.separator + entry.getName());
            try (var bufferOutputStream = new BufferedOutputStream(fileOutputStream, buffer)) {
              int count;
              byte[] data = new byte[buffer];
              while ((count = zipInputStream.read(data, 0, buffer)) != -1) {
                bufferOutputStream.write(data, 0, count);
              }
              bufferOutputStream.flush();
            }
            fileOutputStream.close();
          }
        }
      }
    } catch (IOException e) {
      Debug("Unzip: Archive Error");
      Debug(e.getMessage());
      return false;
    }
    Debug("Unzip: Archive OK");
    return true;
  }
}
