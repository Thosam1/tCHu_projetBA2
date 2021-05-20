package ch.epfl.tchu.gui;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * Une fois les deux ordinateurs connectés au VPN de l'EPFL,
 * le nom (ou l'adresse IP numérique) de l'ordinateur sur lequel le serveur sera exécuté
 * doit être déterminé, ce qui peut se faire au moyen du programme ci-dessous:
 */
public final class ShowMyIpAddress {
    public static void main(String[] args) throws IOException {
        NetworkInterface.networkInterfaces()
                .filter(i -> {
                    try { return i.isUp() && !i.isLoopback(); }
                    catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                })
                .flatMap(NetworkInterface::inetAddresses)
                .filter(a -> a instanceof Inet4Address)
                .map(InetAddress::getCanonicalHostName)
                .forEachOrdered(System.out::println);
    }
}

/**
 * guide :
 * Une des lignes affichées par ce programme devrait commencer par vpn
 * et se terminer par .epfl.ch
 * Il s'agit du nom de l'hôte (l'ordinateur), qui peut être passé en argument au client, comme expliqué plus bas.
 *
 * Une fois obtenu le nom de l'ordinateur—ou l'adresse IP numérique—sur lequel fonctionnera le serveur, ce dernier peut être lancé. Ensuite, le client peut être lancé sur l'autre ordinateur, en lui passant en argument le nom ou l'adresse IP du serveur.
 *
 * et un paragraphe que je n'ai pas compris
 *
 */