package com.leon.lamti.cc.animationAccount;

public class GamesObject {

    private GameObject game1;
    private GameObject game2;
    private GameObject game3;

    public GamesObject () {

    }

    public GamesObject (GameObject game1, GameObject game2, GameObject game3) {

        this.game1 = game1;
        this.game2 = game2;
        this.game3 = game3;
    }

    public GameObject getGame1() {
        return game1;
    }

    public void setGame1(GameObject game1) {
        this.game1 = game1;
    }

    public GameObject getGame2() {
        return game2;
    }

    public void setGame2(GameObject game2) {
        this.game2 = game2;
    }

    public GameObject getGame3() {
        return game3;
    }

    public void setGame3(GameObject game3) {
        this.game3 = game3;
    }
}
