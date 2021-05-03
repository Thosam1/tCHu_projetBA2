package ch.epfl.tchu.gui;

/**
 * @author Thösam Norlha-Tsang (330163)
 */

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.value.ObservableStringValue;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;

/**
 * la partie de l'interface montrant :
 *  en haut: pour chaque joueur, un disque de la couleur de ses wagons, suivi des statistiques le concernant—
 *  nombre de billets et de cartes en main, wagons restants et points de construction obtenus,
 *  en bas: les cinq dernières informations sur le déroulement de la partie.
 */
class InfoViewCreator {
    private InfoViewCreator(){}

    /**
     *
     * @param id
     * @param names
     * @param observableGameState
     * @param infoGame
     * @return la vue des informations
     */
    public VBox createInfoView(PlayerId id, Map<PlayerId, String> names, ObservableGameState observableGameState, List<ObservableStringValue> infoGame){ //todo static ?
        //todo mieux si ça retourne Node ?

        VBox root = new VBox();
        root.getSt
    }
}
