package com.leon.lamti.cc.animationAccount;

/**
 * Created by Timoleon on 9/13/2016.
 */

public class UserObject {

    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String telephone;
    private String address;
    private String city;
    private int interesting;
    private String history;
    private GamesObject games;

    public UserObject ( String email, String password, String firstName, String lastName, String telephone, String address,
        String city, int interesting, String history, GamesObject games ) {

        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.telephone = telephone;
        this.address = address;
        this.city = city;
        this.interesting = interesting;
        this.history = history;
        this.games = games;
    }

    public UserObject () {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getInteresting() {
        return interesting;
    }

    public void setInteresting(int interesting) {
        this.interesting = interesting;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public GamesObject getGames() {
        return games;
    }

    public void setGames(GamesObject games) {
        this.games = games;
    }
}
