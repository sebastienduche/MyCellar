//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2012.03.18 at 05:32:45 PM CET
//


package mycellar;

import mycellar.core.BottlesStatus;
import mycellar.core.IMyCellarObject;
import mycellar.core.MyCellarFields;
import mycellar.core.datas.jaxb.tracks.Track;
import mycellar.core.datas.jaxb.tracks.Tracks;
import mycellar.core.music.MusicSupport;
import mycellar.placesmanagement.Place;
import mycellar.placesmanagement.Rangement;
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
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2021</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.3
 * @since 13/04/21
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "id",
    "title",
    "annee",
    "type",
    "emplacement",
    "numLieu",
    "ligne",
    "colonne",
    "prix",
    "comment",
    "artist",
    "composer",
    "genre",
    "duration",
    "status",
    "lastModified",
    "tracks"
})
@XmlRootElement(name = "Music")
public class Music implements IMyCellarObject, Serializable {

  private static final long serialVersionUID = 7443323147347096231L;

  private int id;

  @XmlElement(required = true)
  private String title;
  @XmlElement(required = true)
  private String annee;
  @XmlElement(required = true)
  private String type;
  @XmlElement(required = true)
  private String emplacement;
  @XmlElement(name = "num_lieu")
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

  public Music() {
    title = type = emplacement = prix = comment = annee = artist = composer = duration = genre = "";
    tracks = null;
    status = "";
    lastModified = null;
  }

  public Music(Music music) {
    Objects.requireNonNull(music);
    id = Program.getNewID();
    title = music.getTitle();
    annee = music.getAnnee();
    type = music.getType();
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
  }

  public Music(MusicBuilder builder) {
    if (builder.id == 0) {
      id = Program.getNewID();
    } else {
      id = builder.id;
    }
    title = builder.nom;
    annee = builder.annee;
    type = builder.type;
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
  public String getType() {
    return type;
  }

  @Override
  public void setType(String type) {
    this.type = type;
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
    String ddMmYyyyHhMm = "dd-MM-yyyy HH:mm";
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(ddMmYyyyHhMm);
    this.lastModified = dateFormat.format(lastModified);
  }

  @Override
  public Rangement getRangement() {
    return Program.getCave(emplacement);
  }

  @Override
  public int getAnneeInt() {
    if (annee.isEmpty()) {
      return 0;
    }
    try {
      int anneeInt = Integer.parseInt(annee);
      return anneeInt;
    } catch(NumberFormatException e) {
      return 0;
    }
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

  @Override
  public double getPriceDouble() {
    String price = Program.convertStringFromHTMLString(prix);
    if (price.isEmpty()) {
      return 0;
    }

    return Program.safeStringToBigDecimal(price, BigDecimal.ZERO).doubleValue();
  }

  @Override
  public BigDecimal getPrice() {
    String price = Program.convertStringFromHTMLString(prix);
    if (price.isEmpty()) {
      return BigDecimal.ZERO;
    }

    return Program.safeStringToBigDecimal(price, BigDecimal.ZERO);
  }

  @Override
  public boolean hasPrice() {
    String price = Program.convertStringFromHTMLString(prix);
    if (price.isEmpty()) {
      return false;
    }
    try {
      Program.stringToBigDecimal(price);
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
  public String toString() {
    return title;
  }

  public void update(final Music music) {
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
    setType(music.getType());
    setDuration(music.getDuration());
    setTracks(music.getTracks());
    if (music.hasNoStatus()) {
      setStatus(BottlesStatus.MODIFIED.name());
    } else {
      setStatus(music.getStatus());
    }
    setLastModified(LocalDateTime.now());
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
        setType(value);
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
      case MATURITY:
        setArtist(value);
        break;
      case PARKER:
        setComposer(value);
        break;
      case VINEYARD:
        if (getTracks() == null) {
          setTracks(new Tracks());
        }
        Program.throwNotImplementedForMusic(this);
//        getTracks().setName(value);
        break;
      case COLOR:
        setDuration(value);
        break;
      case COUNTRY:
        if (getTracks() == null) {
          setTracks(new Tracks());
        }
        Program.throwNotImplementedForMusic(this);
//        getTracks().setCountry(value);
        break;
      case AOC:
        if (getTracks() == null) {
          setTracks(new Tracks());
        }
        Program.throwNotImplementedForMusic(this);
//        getTracks().setAOC(value);
        break;
      case IGP:
        if (getTracks() == null) {
          setTracks(new Tracks());
        }
        Program.throwNotImplementedForMusic(this);
//        getTracks().setIGP(value);
        break;
      case STATUS:
        setStatus(value);
        break;
      default:
        break;
    }
  }

  @Override
  public boolean updateID() {
    if (id != -1) {
      final List<IMyCellarObject> bouteilles = Program.getStorage().getAllList().stream().filter(bouteille -> bouteille.getId() == id).collect(Collectors.toList());
      if(bouteilles.size() == 1 && bouteilles.get(0).equals(this)) {
        return false;
      }
    }
    id = Program.getNewID();
    return true;
  }

  @Override
  public boolean isInTemporaryStock() {
    return Program.TEMP_PLACE.equalsIgnoreCase(emplacement);
  }

  public static Music getBouteilleFromXML(Element bouteilleElem) {
    NodeList nodeId = bouteilleElem.getElementsByTagName("id");
    final int id = Integer.parseInt(nodeId.item(0).getTextContent());
    NodeList nodeName = bouteilleElem.getElementsByTagName("nom");
    final String name = nodeName.item(0).getTextContent();
    NodeList nodeAnnee = bouteilleElem.getElementsByTagName("annee");
    final String year = nodeAnnee.item(0).getTextContent();
    NodeList nodeType = bouteilleElem.getElementsByTagName("type");
    final String type = nodeType.item(0).getTextContent();
    NodeList nodePlace = bouteilleElem.getElementsByTagName("emplacement");
    final String place = nodePlace.item(0).getTextContent();
    NodeList nodeNumLieu = bouteilleElem.getElementsByTagName("num_lieu");
    final int numLieu = Integer.parseInt(nodeNumLieu.item(0).getTextContent());
    NodeList nodeLine = bouteilleElem.getElementsByTagName("ligne");
    final int line = Integer.parseInt(nodeLine.item(0).getTextContent());
    NodeList nodeColumn = bouteilleElem.getElementsByTagName("colonne");
    final int column = Integer.parseInt(nodeColumn.item(0).getTextContent());
    NodeList nodePrice = bouteilleElem.getElementsByTagName("prix");
    final String price = nodePrice.item(0).getTextContent();
    NodeList nodeComment = bouteilleElem.getElementsByTagName("comment");
    final String comment = nodeComment.item(0).getTextContent();
    NodeList nodeArtist = bouteilleElem.getElementsByTagName("artist");
    final String artist = nodeArtist.item(0).getTextContent();
    NodeList nodeComposer = bouteilleElem.getElementsByTagName("composer");
    final String composer = nodeComposer.item(0).getTextContent();
    NodeList nodeGenre = bouteilleElem.getElementsByTagName("genre");
    final String genre = nodeGenre.item(0).getTextContent();
    NodeList nodeDuration = bouteilleElem.getElementsByTagName("duration");
    final String duration = nodeDuration.item(0).getTextContent();
    NodeList nodeTracks = bouteilleElem.getElementsByTagName("tracks");
    List<Track> trackList = new LinkedList<>();
    for (int i = 0; i < nodeTracks.getLength(); i++) {
      final Element tracks = (Element) nodeTracks.item(0);
      NodeList nodeNumber = tracks.getElementsByTagName("number");
      final int trackNumber = Integer.parseInt(nodeNumber.item(0).getTextContent());
      NodeList nodeLabel = tracks.getElementsByTagName("label");
      final String trackLabel = nodeLabel.item(0).getTextContent();
      NodeList nodeTrackDuration = tracks.getElementsByTagName("duration");
      final String trackDuration = nodeTrackDuration.item(0).getTextContent();
      NodeList nodeComnment = tracks.getElementsByTagName("comment");
      final String trackComment = nodeComnment.item(0).getTextContent();
      final Track track = new Track();
      track.setNumber(trackNumber);
      track.setLabel(trackLabel);
      track.setDuration(trackDuration);
      track.setComment(trackComment);
      trackList.add(track);
    }

    NodeList nodeStatus = bouteilleElem.getElementsByTagName("status");
    String status = "";
    if (nodeStatus.getLength() > 0) {
      status = nodeStatus.item(0).getTextContent();
    }
    NodeList nodeLAstModified = bouteilleElem.getElementsByTagName("lastModified");
    String lastModifed = "";
    if (nodeLAstModified.getLength() > 0) {
      lastModifed = nodeLAstModified.item(0).getTextContent();
    }

    return new MusicBuilder(name)
        .id(id)
        .annee(year)
        .supportType(MusicSupport.valueOf(type))
        .place(place)
        .numPlace(numLieu)
        .line(line)
        .column(column)
        .price(price)
        .comment(comment)
        .artist(artist)
        .composer(composer)
        .genre(genre)
        .status(status)
        .lastModified(lastModifed)
        .duration(duration)
        .tracks(trackList)
        .build();
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
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((annee == null) ? 0 : annee.hashCode());
    result = prime * result + colonne;
    result = prime * result + ((duration == null) ? 0 : duration.hashCode());
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
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((tracks == null) ? 0 : tracks.hashCode());
    result = prime * result + ((status == null) ? 0 : status.hashCode());
    result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
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
    if (getClass() != obj.getClass()) {
      return false;
    }
    Music other = (Music) obj;
    if(id != other.id) {
      return false;
    }
    if (annee == null) {
      if (other.annee != null) {
        return false;
      }
    } else if (!annee.equals(other.annee)) {
      return false;
    }
    if (colonne != other.colonne) {
      return false;
    }
    if (duration == null) {
      if (other.duration != null) {
        return false;
      }
    } else if (!duration.equals(other.duration)) {
      return false;
    }
    if (comment == null) {
      if (other.comment != null) {
        return false;
      }
    } else if (!comment.equals(other.comment)) {
      return false;
    }
    if (emplacement == null) {
      if (other.emplacement != null) {
        return false;
      }
    } else if (!emplacement.equals(other.emplacement)) {
      return false;
    }
    if (ligne != other.ligne) {
      return false;
    }
    if (artist == null) {
      if (other.artist != null) {
        return false;
      }
    } else if (!artist.equals(other.artist)) {
      return false;
    }
    if (title == null) {
      if (other.title != null) {
        return false;
      }
    } else if (!title.equals(other.title)) {
      return false;
    }
    if (numLieu != other.numLieu) {
      return false;
    }
    if (composer == null) {
      if (other.composer != null) {
        return false;
      }
    } else if (!composer.equals(other.composer)) {
      return false;
    }
    if (genre == null) {
      if (other.genre != null) {
        return false;
      }
    } else if (!genre.equals(other.genre)) {
      return false;
    }
    if (prix == null) {
      if (other.prix != null) {
        return false;
      }
    } else if (!prix.equals(other.prix)) {
      return false;
    }
    if (type == null) {
      if (other.type != null) {
        return false;
      }
    } else if (!type.equals(other.type)) {
      return false;
    }
    if (tracks == null) {
      if (other.tracks != null) {
        return false;
      }
    } else if (!tracks.equals(other.tracks)) {
      return false;
    }
    if (status == null) {
      if (other.status != null) {
        return false;
      }
    } else if (!status.equals(other.status)) {
      return false;
    }
    if (lastModified == null) {
      if (other.lastModified != null) {
        return false;
      }
    } else if (!lastModified.equals(other.lastModified)) {
      return false;
    }
    return true;
  }

  @Override
  public boolean isInExistingPlace() {
    return Program.isExistingPlace(emplacement);
  }


  public static class MusicBuilder {
    private int id;
    private final String nom;
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

    public MusicBuilder(String nom) {
      this.nom = nom;
      id = numLieu = ligne = colonne = 0;
      type = emplacement = prix = comment = annee = artist = composer = duration = genre = "";
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

    public MusicBuilder supportType(MusicSupport musicSupport) {
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
