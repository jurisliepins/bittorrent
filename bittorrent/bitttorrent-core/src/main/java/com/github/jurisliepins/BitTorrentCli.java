package com.github.jurisliepins;

import com.github.jurisliepins.info.MetaInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class App {
    

    public static void main(String[] args) throws IOException {
        var bytes = Files.readAllBytes(Paths.get("/Users/jurisliepins/Downloads/ubuntu-24.04-desktop-amd64.iso.torrent"));

        var mapper = new BObjectMapper();
        var mi = mapper.readFromBytes(bytes, MetaInfo.class);

        System.out.println(mi);
    }
}
