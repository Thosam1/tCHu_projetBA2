package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;

class MapViewCreator {
    Pane pane;
    Node node;
    private MapViewCreator() {
        
    }
    
    public void createMapView(ObservableGameState observableGame, 
            ObjectProperty<ClaimRouteHandler> objectProperty,
            CardChooser chooser) {
        pane = new BorderPane(); //plutot Pane
        ImageView view = new ImageView();
    }
    
    /**
     * interface fonctionnelle imbriquée dans la classe
     * Il s'agit d'un sélectionneur de cartes
     * */
    @FunctionalInterface
    interface CardChooser {
      void chooseCards(List<SortedBag<Card>> options,
               ChooseCardsHandler handler);
    }
}
