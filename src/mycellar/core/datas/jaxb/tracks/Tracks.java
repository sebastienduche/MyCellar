package mycellar.core.datas.jaxb.tracks;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "tracks")
@XmlAccessorType (XmlAccessType.FIELD)
public class Tracks
{
  @XmlElement(name = "track")
  private List<Track> tracks = null;

  public List<Track> getTracks() {
    return tracks;
  }

  public void setTracks(List<Track> tracks) {
    this.tracks = tracks;
  }
}
