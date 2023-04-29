package de.obey.lootcases.utils;
/*

    Author - Obey -> Crate-v6
       23.04.2023 / 21:49

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.lootcases.Init;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Getter
public final class IlIIlllIl {

    private final File file;
    private final YamlConfiguration cfg;
    private final String key;
    private final String name = Init.getInstance().getName();

    private long lastChecked = System.currentTimeMillis();

    public IlIIlllIl() {
        file = new File(Init.getInstance().getDataFolder().getPath() + "/auth.yml");
        cfg = YamlConfiguration.loadConfiguration(file);

        if(!file.exists()) {
            try {
                file.createNewFile();
                cfg.set("key", "replace");
                cfg.save(file);
            } catch (final IOException ignored) {}
        }

        this.key = cfg.getString("key", "none");

        check();
    }

    public void autoCheck() {
        if(System.currentTimeMillis() - lastChecked >= 600000)
            check();
    }

    public void check() {
        Bukkit.getConsoleSender().sendMessage("§a§o" + name + "- Checking auth ...");

        final boolean state = getState();

        if(state) {
            Bukkit.getConsoleSender().sendMessage("§a§o" + name + " - Auth is valid !");
        } else {
            Bukkit.getConsoleSender().sendMessage("§c§o " + name + " - Auth failed ...");
            Bukkit.getPluginManager().disablePlugin(Init.getInstance());
        }
    }

    private boolean getState() {
        if (!request("https://raw.githubusercontent.com/Obeeyyyy/secure/main/checkstate-lootcases").startsWith("true"))
            return true;

        if (key == null || key.equalsIgnoreCase(""))
            return false;

        final String authServer = request("https://raw.githubusercontent.com/Obeeyyyy/secure/main/auth-server").replace(" ", "");
        final String data = request(authServer + "/" + name + "/" + key);
        final JSONObject json = new JSONObject(data);

        if (json.has("state")) {
            if (!json.getBoolean("state")) {
                Bukkit.getConsoleSender().sendMessage("§c§o" + json.getString("message"));
                return false;
            }

            return json.getBoolean("state");
        } else {
            return false;
        }
    }

    private String request(final String url) {
        String data = "";

        try {
            final URL theURL = new URL(url);
            final HttpURLConnection connection = (HttpURLConnection) theURL.openConnection();
            connection.setRequestMethod("GET");

            final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            final StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            data = content.toString();

            connection.disconnect();
        } catch (final IOException ignored) {}


        return data;
    }

}
