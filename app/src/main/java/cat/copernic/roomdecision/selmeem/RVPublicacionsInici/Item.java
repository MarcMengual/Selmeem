package cat.copernic.roomdecision.selmeem.RVPublicacionsInici;

public class Item {

    String titol;
    String propietari;
    String imatge;
    int likes;

    public Item(String titol, String propietari, String imatge, int likes){
        this.titol = titol;
        this.propietari = propietari;
        this.imatge = imatge;
        this.likes = likes;
    }

    public String getTitol() {
        return titol;
    }

    public void setTitol(String titol) {
        this.titol = titol;
    }

    public String getPropietari() {
        return propietari;
    }

    public void setPropietari(String propietari) {
        this.propietari = propietari;
    }

    public String getImatge() {
        return imatge;
    }

    public void setImatge(String imatge) {
        this.imatge = imatge;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}

