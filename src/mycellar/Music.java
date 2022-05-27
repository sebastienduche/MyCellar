package mycellar;

import mycellar.core.BottlesStatus;
import mycellar.core.MyCellarObject;
import mycellar.core.common.MyCellarFields;
import mycellar.core.common.music.MusicSupport;
import mycellar.core.datas.jaxb.tracks.Track;
import mycellar.core.datas.jaxb.tracks.Tracks;
import mycellar.placesmanagement.Place;
import mycellar.placesmanagement.Rangement;
import mycellar.placesmanagement.RangementUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static mycellar.MyCellarUtils.assertObjectType;
import static mycellar.ProgramConstants.DATE_FORMATER_DD_MM_YYYY_HH_MM;
import static mycellar.ProgramConstants.DOUBLE_DOT;
import static mycellar.general.XmlUtils.getTextContent;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2021
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.5
 * @since 27/05/22
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "id",
    "externalId",
    "title",
    "annee",
    "kind",
    "diskNumber",
    "diskCount",
    "rating",
    "file",
    "emplacement",
    "numLieu",
    "ligne",
    "colonne",
    "prix",
    "comment",
    "artist",
    "composer",
    "genre",
    "album",
    "duration",
    "status",
    "lastModified",
    "tracks"
})
@XmlRootElement(name = "Music")
public class Music extends MyCellarObject implements Serializable {

  private static final long serialVersionUID = 7443323147347096231L;

  private int id;
  private int externalId;

  @XmlElement(required = true)
  private String title;
  @XmlElement(required = true)
  private String annee;
  @XmlElement(required = true)
  private String kind;
  @XmlElement(required = true)
  private String emplacement;
  private int numLieu;
  private int ligne;
  private int colonne;
  @XmlElement(required = true)
  private String prix;
  @XmlElement(required = true)
  private String comment;
  @XmlElement(required = true)
  private String artist;
  @XmlElement(required = true)
  private String composer;
  @XmlElement(required = true)
  private String genre;
  @XmlElement()
  private Tracks tracks;
  @XmlElement()
  private String duration;
  @XmlElement()
  private String status;
  @XmlElement()
  private String lastModified;
  @XmlElement()
  private int diskNumber;
  @XmlElement()
  private int diskCount;
  @XmlElement()
  private int rating;
  @XmlElement()
  private String file;
  @XmlElement()
  private String album;

  public Music() {
    title = kind = emplacement = prix = comment = annee = artist = composer = duration = genre = file = album = "";
    tracks = null;
    status = "";
    lastModified = null;
  }

  public Music(Music music) {
    Objects.requireNonNull(music);
    id = Program.getNewID();
    externalId = music.getExternalId();
    title = music.getTitle();
    annee = music.getAnnee();
    kind = music.getKind();
    emplacement = music.getEmplacement();
    numLieu = music.getNumLieu();
    ligne = music.getLigne();
    colonne = music.getColonne();
    prix = music.getPrix();
    comment = music.getComment();
    artist = music.getArtist();
    composer = music.getComposer();
    genre = music.getGenre();
    duration = music.getDuration();
    tracks = music.getTracks();
    status = music.getStatus();
    lastModified = music.getLastModified();
    diskNumber = music.getDiskNumber();
    diskCount = music.getDiskCount();
    rating = music.getRating();
    file = music.getFile();
    album = music.getAlbum();
  }

  public Music(MusicBuilder builder) {
    if (builder.id == 0) {
      id = Program.getNewID();
    } else {
      id = builder.id;
    }
    externalId = builder.externalId;
    title = builder.nom;
    annee = builder.annee;
    kind = builder.type;
    emplacement = builder.emplacement;
    numLieu = builder.numLieu;
    ligne = builder.ligne;
    colonne = builder.colonne;
    prix = builder.prix;
    comment = builder.comment;
    artist = builder.artist;
    composer = builder.composer;
    genre = builder.genre;
    duration = builder.duration;
    tracks = builder.tracks;
    status = builder.status;
    lastModified = builder.lastModified;
    diskNumber = builder.diskNumber;
    diskCount = builder.diskCount;
    rating = builder.rating;
    file = builder.file;
    album = builder.album;
  }

  public static boolean isInvalidYear(String year) {
    year = year.strip();
    if (!Program.hasYearControl()) {
      return false;
    }
    int n;
    try {
      n = Integer.parseInt(year);
    } catch (NumberFormatException e) {
      Debug("ERROR: Unable to parse year '" + year + "'!");
      return true;
    }

    int current_year = LocalDate.now().getYear();
    return year.length() == 4 && n > current_year;
  }

  public static Music fromXml(Element element) {
    return new Music().fromXmlElemnt(element);
  }

  /**
   * Debug
   *
   * @param sText String
   */
  private static void Debug(String sText) {
    Program.Debug("Music: " + sText);
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  @Override
  public String getNom() {
    return getTitle();
  }

  @Override
  public void setNom(String value) {
    setTitle(value);
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public String getAnnee() {
    return annee;
  }

  @Override
  public void setAnnee(String annee) {
    this.annee = annee;
  }

  @Override
  public String getKind() {
    return kind;
  }

  @Override
  public void setKind(String kind) {
    this.kind = kind;
  }

  @Override
  public String getEmplacement() {
    return emplacement;
  }

  @Override
  public void setEmplacement(String emplacement) {
    this.emplacement = emplacement;
  }

  @Override
  public int getNumLieu() {
    return numLieu;
  }

  @Override
  public void setNumLieu(int numLieu) {
    this.numLieu = numLieu;
  }

  @Override
  public int getLigne() {
    return ligne;
  }

  @Override
  public void setLigne(int ligne) {
    this.ligne = ligne;
  }

  @Override
  public int getColonne() {
    return colonne;
  }

  @Override
  public void setColonne(int colonne) {
    this.colonne = colonne;
  }

  @Override
  public String getPrix() {
    return prix;
  }

  public void setPrix(String prix) {
    this.prix = prix;
  }

  @Override
  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getArtist() {
    return artist;
  }

  public void setArtist(String artist) {
    this.artist = artist;
  }

  public String getComposer() {
    return composer;
  }

  public void setComposer(String composer) {
    this.composer = composer;
  }

  public String getDuration() {
    return duration;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }

  public Tracks getTracks() {
    return tracks;
  }

  public void setTracks(Tracks tracks) {
    this.tracks = tracks;
  }

  public String getGenre() {
    return genre;
  }

  public void setGenre(String genre) {
    this.genre = genre;
  }

  @Override
  public String getStatus() {
    return status;
  }

  @Override
  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public String getLastModified() {
    return lastModified;
  }

  private void setLastModified(LocalDateTime lastModified) {
    this.lastModified = DATE_FORMATER_DD_MM_YYYY_HH_MM.format(lastModified);
  }

  public int getDiskNumber() {
    return diskNumber;
  }

  public void setDiskNumber(int diskNumber) {
    this.diskNumber = diskNumber;
  }

  public int getDiskCount() {
    return diskCount;
  }

  public void setDiskCount(int diskCount) {
    this.diskCount = diskCount;
  }

  public int getRating() {
    return rating;
  }

  public void setRating(int rating) {
    this.rating = rating;
  }

  public String getFile() {
    return file;
  }

  public void setFile(String file) {
    this.file = file;
  }

  public int getExternalId() {
    return externalId;
  }

  public void setExternalId(int externalId) {
    this.externalId = externalId;
  }

  public String getAlbum() {
    return album;
  }

  public void setAlbum(String album) {
    this.album = album;
  }

  @Override
  public Rangement getRangement() {
    return (Rangement)Program.getPlaceByName(emplacement);
  }

  @Override
  public int getAnneeInt() {
    if (annee.isEmpty()) {
      return 0;
    }
    try {
      int anneeInt = Integer.parseInt(annee);
      return anneeInt;
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  public String getFormattedDuration() {
    if (duration != null) {
      int value = MyCellarUtils.safeParseInt(duration, 0);
      value /= 1000;
      int hour = value / 3600;
      int newValue = value - (hour * 3600);
      int minute = newValue / 60;
      int second = newValue - (minute * 60);
      if (hour > 0) {
        return hour + DOUBLE_DOT + StringUtils.leftPad(Integer.toString(minute), 2, "0") + DOUBLE_DOT + StringUtils.leftPad(Integer.toString(second), 2, "0");
      } else {
        return minute + DOUBLE_DOT + StringUtils.leftPad(Integer.toString(second), 2, "0");
      }
    }
    return duration;
  }

  @Override
  public double getPriceDouble() {
    return getPrice().doubleValue();
  }

  @Override
  public BigDecimal getPrice() {
    String price = MyCellarUtils.convertStringFromHTMLString(prix);
    if (price.isEmpty()) {
      return BigDecimal.ZERO;
    }

    return MyCellarUtils.safeStringToBigDecimal(price, BigDecimal.ZERO);
  }

  @Override
  public boolean hasPrice() {
    if (prix.isBlank()) {
      return false;
    }
    try {
      MyCellarUtils.stringToBigDecimal(MyCellarUtils.convertStringFromHTMLString(prix));
    } catch (NumberFormatException ignored) {
      return false;
    }
    return true;
  }

  @Override
  public Place getPlace() {
    return new Place.PlaceBuilder(getRangement())
        .withNumPlace(getNumLieu())
        .withLine(getLigne())
        .withColumn(getColonne())
        .build();
  }

  @Override
  public boolean isNonVintage() {
    return false;
  }

  public MusicSupport getMusicSupport() {
    return MusicSupport.getSupport(getKind());
  }

  public void setMusicSupport(MusicSupport musicSupport) {
    setKind(musicSupport.name());
  }

  @Override
  public String toString() {
    return title;
  }

  @Override
  public void setModified() {
    setLastModified(LocalDateTime.now());
  }

  @Override
  public void setCreated() {
    setStatus(BottlesStatus.CREATED.name());
    setLastModified(LocalDateTime.now());
  }

  @Override
  public boolean hasNoStatus() {
    return status.isEmpty() || status.equals(BottlesStatus.NONE.name());
  }

  private boolean canChangeStatus() {
    return status.isEmpty() || status.equals(BottlesStatus.NONE.name()) || status.equals(BottlesStatus.CREATED.name());
  }

  @Override
  public void updateStatus() {
    if (canChangeStatus()) {
      status = BottlesStatus.MODIFIED.name();
    }
    setModified();
  }

  @Override
  public void setValue(MyCellarFields field, String value) {
    setModified();
    switch (field) {
      case NAME:
        setTitle(value);
        break;
      case YEAR:
        setAnnee(value);
        break;
      case TYPE:
        setKind(value);
        break;
      case PLACE:
        setEmplacement(value);
        break;
      case NUM_PLACE:
        setNumLieu(Double.valueOf(value).intValue());
        break;
      case LINE:
        setLigne(Double.valueOf(value).intValue());
        break;
      case COLUMN:
        setColonne(Double.valueOf(value).intValue());
        break;
      case PRICE:
        setPrix(value);
        break;
      case COMMENT:
        setComment(value);
        break;
      case ARTIST:
        setArtist(value);
        break;
      case DURATION:
        setDuration(value);
        break;
      case COMPOSER:
        setComposer(value);
        break;
      case STATUS:
        setStatus(value);
        break;
      case DISK_NUMBER:
        setDiskNumber(Double.valueOf(value).intValue());
        break;
      case DISK_COUNT:
        setDiskCount(Double.valueOf(value).intValue());
        break;
      case RATING:
        setRating(Double.valueOf(value).intValue());
        break;
      case FILE:
        setFile(value);
        break;
      case EXTERNAL_ID:
        setExternalId(Integer.parseInt(value));
        break;
      case ALBUM:
        setAlbum(value);
        break;
      case AOC:
      case IGP:
      case PARKER:
      case VINEYARD:
      case MATURITY:
        Program.throwNotImplementedIfNotFor(this, Bouteille.class);
        break;
      default:
        break;
    }
  }

  @Override
  public boolean updateID() {
    if (id != -1) {
      final List<MyCellarObject> bouteilles = Program.getStorage().getAllList().stream().filter(bouteille -> bouteille.getId() == id).collect(Collectors.toList());
      if (bouteilles.size() == 1 && bouteilles.get(0).equals(this)) {
        return false;
      }
    }
    id = Program.getNewID();
    return true;
  }

  @Override
  public boolean isInTemporaryStock() {
    return RangementUtils.isTemporaryPlace(emplacement);
  }

  @Override
  public Music fromXmlElemnt(Element element) {
    final int elemId = Integer.parseInt(getTextContent(element.getElementsByTagName("id"), "-1"));
    final int elemExternalId = Integer.parseInt(getTextContent(element.getElementsByTagName("external_id"), "-1"));
    final String name = getTextContent(element.getElementsByTagName("title"));
    final String year = getTextContent(element.getElementsByTagName("annee"));
    final String type = getTextContent(element.getElementsByTagName("type"));
    final String elemAlbum = getTextContent(element.getElementsByTagName("album"));
    final String place = getTextContent(element.getElementsByTagName("emplacement"));
    final int elemNumLieu = Integer.parseInt(getTextContent(element.getElementsByTagName("num_lieu"), "0"));
    final int line = Integer.parseInt(getTextContent(element.getElementsByTagName("ligne"), "0"));
    final int column = Integer.parseInt(getTextContent(element.getElementsByTagName("colonne"), "0"));
    final String price = getTextContent(element.getElementsByTagName("prix"));
    final String elemComment = getTextContent(element.getElementsByTagName("comment"));
    final String elemArtist = getTextContent(element.getElementsByTagName("artist"));
    final String elemComposer = getTextContent(element.getElementsByTagName("composer"));
    final String elemGenre = getTextContent(element.getElementsByTagName("genre"));
    final String elemDuration = getTextContent(element.getElementsByTagName("duration"));
    NodeList nodeTracks = element.getElementsByTagName("tracks");
    List<Track> trackList = new LinkedList<>();
    for (int i = 0; i < nodeTracks.getLength(); i++) {
      final Element elemTracks = (Element) nodeTracks.item(i);
      final int trackNumber = Integer.parseInt(getTextContent(elemTracks.getElementsByTagName("number"), "0"));
      final String trackLabel = getTextContent(elemTracks.getElementsByTagName("label"));
      final String trackDuration = getTextContent(elemTracks.getElementsByTagName("duration"));
      final String trackComment = getTextContent(elemTracks.getElementsByTagName("comment"));
      final Track track = new Track();
      track.setNumber(trackNumber);
      track.setLabel(trackLabel);
      track.setDuration(trackDuration);
      track.setComment(trackComment);
      trackList.add(track);
    }

    String elemStatus = getTextContent(element.getElementsByTagName("status"));
    String lastModifed = getTextContent(element.getElementsByTagName("lastModified"));
    String elemFile = getTextContent(element.getElementsByTagName("file"));
    int elemDiskNumber = Integer.parseInt(getTextContent(element.getElementsByTagName("diskNumber"), "1"));
    int elemDiskCount = Integer.parseInt(getTextContent(element.getElementsByTagName("diskCount"), "1"));
    int elemRating = Integer.parseInt(getTextContent(element.getElementsByTagName("rating"), "0"));

    return new MusicBuilder(name)
        .id(elemId)
        .externalId(elemExternalId)
        .annee(year)
        .musicSupport(MusicSupport.valueOf(type))
        .place(place)
        .numPlace(elemNumLieu)
        .line(line)
        .column(column)
        .price(price)
        .comment(elemComment)
        .artist(elemArtist)
        .composer(elemComposer)
        .genre(elemGenre)
        .status(elemStatus)
        .lastModified(lastModifed)
        .duration(elemDuration)
        .tracks(trackList)
        .diskNumber(elemDiskNumber)
        .diskCount(elemDiskCount)
        .rating(elemRating)
        .file(elemFile)
        .album(elemAlbum)
        .build();
  }

  @Override
  public Music cast(MyCellarObject myCellarObject) {
    assertObjectType(myCellarObject, Music.class);
    return (Music) myCellarObject;
  }

  @Override
  public Music castCopy(MyCellarObject myCellarObject) {
    assertObjectType(myCellarObject, Music.class);
    return new Music((Music) myCellarObject);
  }

  @Override
  public void update(MyCellarObject myCellarObject) {
    Music music = (Music) myCellarObject;
    setTitle(music.getTitle());
    setAnnee(music.getAnnee());
    setColonne(music.getColonne());
    setComment(music.getComment());
    setEmplacement(music.getEmplacement());
    setLigne(music.getLigne());
    setArtist(music.getArtist());
    setNumLieu(music.getNumLieu());
    setComposer(music.getComposer());
    setGenre(music.getGenre());
    setPrix(music.getPrix());
    setKind(music.getKind());
    setDuration(music.getDuration());
    setTracks(music.getTracks());
    if (music.hasNoStatus()) {
      setStatus(BottlesStatus.MODIFIED.name());
    } else {
      setStatus(music.getStatus());
    }
    setLastModified(LocalDateTime.now());
    setDiskCount(music.getDiskCount());
    setDiskNumber(music.getDiskNumber());
    setRating(music.getRating());
    setFile(music.getFile());
    setExternalId(music.getExternalId());
    setAlbum(music.getAlbum());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((annee == null) ? 0 : annee.hashCode());
    result = prime * result + externalId;
    result = prime * result + colonne;
    result = prime * result + diskCount;
    result = prime * result + diskNumber;
    result = prime * result + rating;
    result = prime * result + ((duration == null) ? 0 : duration.hashCode());
    result = prime * result + ((file == null) ? 0 : file.hashCode());
    result = prime * result + ((comment == null) ? 0 : comment.hashCode());
    result = prime * result
        + ((emplacement == null) ? 0 : emplacement.hashCode());
    result = prime * result + ligne;
    result = prime * result
        + ((artist == null) ? 0 : artist.hashCode());
    result = prime * result + ((title == null) ? 0 : title.hashCode());
    result = prime * result + numLieu;
    result = prime * result + ((composer == null) ? 0 : composer.hashCode());
    result = prime * result + ((genre == null) ? 0 : genre.hashCode());
    result = prime * result + ((prix == null) ? 0 : prix.hashCode());
    result = prime * result + ((kind == null) ? 0 : kind.hashCode());
    result = prime * result + ((tracks == null) ? 0 : tracks.hashCode());
    result = prime * result + ((status == null) ? 0 : status.hashCode());
    result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
    result = prime * result + ((album == null) ? 0 : album.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!Objects.equals(getClass(), obj.getClass())) {
      return false;
    }
    Music other = (Music) obj;
    if (id != other.id) {
      return false;
    }
    if (externalId != other.externalId) {
      return false;
    }
    if (equalsValue(annee, other.annee)) return false;
    if (equalsValue(duration, other.duration)) return false;
    if (equalsValue(comment, other.comment)) return false;
    if (equalsValue(emplacement, other.emplacement)) return false;
    if (equalsValue(artist, other.artist)) return false;
    if (equalsValue(title, other.title)) return false;
    if (equalsValue(composer, other.composer)) return false;
    if (equalsValue(genre, other.genre)) return false;
    if (equalsValue(prix, other.prix)) return false;
    if (equalsValue(kind, other.kind)) return false;
    if (colonne != other.colonne) {
      return false;
    }
    if (ligne != other.ligne) {
      return false;
    }
    if (numLieu != other.numLieu) {
      return false;
    }
    if (tracks == null) {
      if (other.tracks != null) {
        return false;
      }
    } else if (!tracks.equals(other.tracks)) {
      return false;
    }
    return !equalsValue(status, other.status) &&
        !equalsValue(lastModified, other.lastModified) &&
        diskNumber == other.diskNumber &&
        diskCount == other.diskCount &&
        rating == other.rating &&
        !equalsValue(file, other.file);
  }

  @Override
  public boolean isInExistingPlace() {
    return RangementUtils.isExistingPlace(emplacement);
  }


  public static class MusicBuilder {
    private final String nom;
    private int externalId;
    private int id;
    private String annee;
    private String type;
    private String emplacement;
    private int numLieu;
    private int ligne;
    private int colonne;
    private String prix;
    private String comment;
    private String artist;
    private String composer;
    private String genre;
    private String duration;
    private Tracks tracks;
    private String status;
    private String lastModified;
    private int diskNumber;
    private int diskCount;
    private int rating;
    private String file;
    private String album;

    public MusicBuilder(String nom) {
      this.nom = nom;
      id = numLieu = ligne = colonne = externalId = 0;
      type = emplacement = prix = comment = annee = artist = composer = duration = genre = file = album = "";
      tracks = null;
      status = "";
      lastModified = null;
    }

    private MusicBuilder id(int id) {
      this.id = id;
      return this;
    }

    public MusicBuilder annee(String annee) {
      this.annee = annee;
      return this;
    }

    public MusicBuilder musicSupport(MusicSupport musicSupport) {
      type = musicSupport.name();
      return this;
    }

    public MusicBuilder place(String place) {
      emplacement = place;
      return this;
    }

    public MusicBuilder numPlace(int num) {
      numLieu = num;
      return this;
    }

    public MusicBuilder line(int num) {
      ligne = num;
      return this;
    }

    public MusicBuilder column(int num) {
      colonne = num;
      return this;
    }

    public MusicBuilder price(String price) {
      prix = price;
      return this;
    }

    public MusicBuilder comment(String comment) {
      this.comment = comment;
      return this;
    }

    public MusicBuilder artist(String artist) {
      this.artist = artist;
      return this;
    }

    public MusicBuilder composer(String composer) {
      this.composer = composer;
      return this;
    }

    public MusicBuilder genre(String genre) {
      this.genre = genre;
      return this;
    }

    public MusicBuilder duration(String duration) {
      this.duration = duration;
      return this;
    }

    public MusicBuilder status(String status) {
      this.status = status;
      return this;
    }

    public MusicBuilder lastModified(String lastModified) {
      this.lastModified = lastModified;
      return this;
    }

    public MusicBuilder diskNumber(int diskNumber) {
      this.diskNumber = diskNumber;
      return this;
    }

    public MusicBuilder diskCount(int diskCount) {
      this.diskCount = diskCount;
      return this;
    }

    public MusicBuilder rating(int rating) {
      this.rating = rating;
      return this;
    }

    public MusicBuilder file(String file) {
      this.file = file;
      return this;
    }

    public MusicBuilder externalId(int externalId) {
      this.externalId = externalId;
      return this;
    }

    public MusicBuilder album(String album) {
      this.album = album;
      return this;
    }

    public MusicBuilder tracks(List<Track> trackList) {
      if (tracks == null) {
        tracks = new Tracks();
        tracks.setTracks(trackList);
      } else {
        tracks.getTracks().addAll(trackList);
      }
      return this;
    }

    public MusicBuilder track(int number, String label, String duration, String comment) {
      if (tracks == null) {
        tracks = new Tracks();
        tracks.setTracks(new LinkedList<>());
      }
      final Track track = new Track();
      track.setNumber(number);
      track.setLabel(label);
      track.setDuration(duration);
      track.setComment(comment);
      tracks.getTracks().add(track);
      return this;
    }

    public Music build() {
      return new Music(this);
    }
  }

}
