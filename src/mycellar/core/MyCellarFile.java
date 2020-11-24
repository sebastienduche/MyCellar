package mycellar.core;

import mycellar.Program;
import mycellar.core.datas.MyCellarBottleContenance;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2020</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.2
 * @since 24/11/20
 */
public class MyCellarFile {

  private File file;
  private final MyLinkedHashMap caveConfig;

  private boolean valid = false;

  public MyCellarFile(File file) {
    this.file = file;
    caveConfig = new MyLinkedHashMap();
  }

  public void unzip() throws UnableToOpenFileException {
    if(!file.exists()) {
      throw new UnableToOpenFileException("File doesn't exist: " + file.getAbsolutePath());
    }
    try {
      // Dezippage
      final String workDir = Program.getWorkDir(false);
      boolean unzipOK = unzipDir(workDir);
      final String absolutePath = file.getAbsolutePath();
      Debug("Unzipping " + absolutePath + " to " +workDir + (unzipOK ? " OK" : " KO"));
      if (!unzipOK) {
        valid = false;
        throw new UnableToOpenFileException("Unzipping error for file: " + absolutePath);
      }
    } catch (Exception e) {
      Debug("ERROR: Unable to unzip file " + file.getAbsolutePath());
      Program.showException(e, false);
      valid = false;
      throw new UnableToOpenFileException("Unzipping error: " + e.getMessage());
    }
    valid = true;
  }

  public boolean exists() {
    return file.exists() && file.canWrite();
  }

  public boolean isFileSavable() {
    return !file.getName().endsWith(Program.UNTITLED1_SINFO) && exists();
  }

  public boolean isValid() {
    return valid;
  }

  public void save() {
    saveAs(file);
  }

  public void saveAs(File newFile) {
    saveCaveProperties();
    zipDir(newFile);
    file = newFile;
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

  private static void Debug(String sText) {
    Program.Debug("MyCellarFile: " + sText);
  }

  /**
   * unzipDir: Dezippe une archive dans un repertoire
   *
   * @param dest_dir String
   * @return boolean
   */
  private boolean unzipDir(String dest_dir) {
    try {
      String fileName = file.getAbsolutePath();
      Debug("Unzip: Archive " + fileName);
      // ouverture fichier entree

      var fileInputStream = new FileInputStream(file);
      // ouverture fichier de buffer
      var bufferedInputStream = new BufferedInputStream(fileInputStream);
      // ouverture archive Zip d'entree
      try(var zipInputStream = new ZipInputStream(bufferedInputStream)) {
        // entree Zip
        ZipEntry entry;
        // parcours des entrees de l'archive
        int BUFFER = 2048;
        while ((entry = zipInputStream.getNextEntry()) != null) {
          // affichage du nom de l'entree
          // creation fichier
          File f = new File(dest_dir);
          boolean ok = true;
          if (!f.exists()) {
            ok = f.mkdir();
          }
          if (ok) {
            var fileOutputStream = new FileOutputStream(dest_dir + File.separator + entry.getName());
            Debug("Unzip: File " + dest_dir + File.separator + entry.getName());
            // affectation buffer de sortie
            try (var bufferOutputStream = new BufferedOutputStream(fileOutputStream, BUFFER)) {
              // ecriture sur disque
              int count;
              byte[] data = new byte[BUFFER];
              while ((count = zipInputStream.read(data, 0, BUFFER)) != -1) {
                bufferOutputStream.write(data, 0, count);
              }
              // vidage du tampon
              bufferOutputStream.flush();
            }
            fileOutputStream.close();
          }
        }
      }
      bufferedInputStream.close();
      fileInputStream.close();
    }
    catch (Exception e) {
      Debug("Unzip: Archive Error");
      Debug(e.getMessage());
      return false;
    }
    Debug("Unzip: Archive OK");
    return true;
  }

  /**
   * zipDir: Compression de repertoire
   *
   * @param fileName File
   * @return boolean
   */
  private static void zipDir(File fileName) {

    final String workDir = Program.getWorkDir(false);
    Debug("zipDir: Zipping in " + workDir + " with archive " + fileName);
    try {
      // creation d'un flux d'ecriture sur fichier
      var dest = new FileOutputStream(fileName);
      // calcul du checksum : Adler32 (plus rapide) ou CRC32
      var checksum = new CheckedOutputStream(dest, new Adler32());
      // creation d'un buffer d'ecriture
      var buff = new BufferedOutputStream(checksum);
      // creation d'un flux d'ecriture Zip
      try(var out = new ZipOutputStream(buff)) {
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
            final String workDir1 = Program.getWorkDir(true);
            f = new File(workDir1 + file);
            if (f.isDirectory() || Program.UNTITLED1_SINFO.compareTo(file) == 0) {
              continue;
            }
            // creation d'un flux de lecture
            var inputStream = new FileInputStream(workDir1 + file);
            // creation d'un tampon de lecture sur ce flux
            try (var bufferedInputStream = new BufferedInputStream(inputStream, BUFFER)) {
              // creation d'en entree Zip pour ce fichier
              String name = Program.removeAccents(file);
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
    }
    catch (Exception e) {
      Debug("zipDir: Error while zipping");
      Program.showException(e, false);
    }
    Debug("zipDir OK");
  }
}
