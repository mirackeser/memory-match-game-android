package com.example.hafizaoyunu.model;

import java.util.Arrays;
import java.util.List;

/**
 * Kart temalarını tanımlayan enum
 * Her tema farklı emoji/sembol setleri içerir
 */
public enum Theme {
    EMOJI("Emojiler", Arrays.asList(
            "😀", "😎", "🥳", "😍", "🤩", "😇", "🥰", "😋", "🤗", "😜",
            "🦊", "🐶", "🐱", "🐼", "🦁", "🐯", "🐻", "🐨", "🐰", "🐸",
            "🌟", "🌈", "🌸", "🍀", "🌺", "🌻", "🍎", "🍕", "🎸", "🎨",
            "🚀", "✈️", "🎠", "🎡", "🎪", "🏆", "⚽", "🎯", "🎮", "🎲"
    )),
    
    ANIMALS("Hayvanlar", Arrays.asList(
            "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯",
            "🦁", "🐮", "🐷", "🐸", "🐵", "🐔", "🐧", "🐦", "🦆", "🦅",
            "🦉", "🦇", "🐺", "🐗", "🐴", "🦄", "🐝", "🐛", "🦋", "🐌",
            "🐞", "🐜", "🦟", "🦗", "🐢", "🐍", "🦎", "🦖", "🦕", "🐙"
    )),
    
    NUMBERS("Sayılar", Arrays.asList(
            "1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣", "🔟",
            "①", "②", "③", "④", "⑤", "⑥", "⑦", "⑧", "⑨", "⑩",
            "⓵", "⓶", "⓷", "⓸", "⓹", "⓺", "⓻", "⓼", "⓽", "⓾",
            "Ⅰ", "Ⅱ", "Ⅲ", "Ⅳ", "Ⅴ", "Ⅵ", "Ⅶ", "Ⅷ", "Ⅸ", "Ⅹ"
    )),
    
    FOODS("Yiyecekler", Arrays.asList(
            "🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🫐", "🍈",
            "🍒", "🍑", "🥭", "🍍", "🥥", "🥝", "🍅", "🥑", "🥦", "🥬",
            "🌽", "🥕", "🧅", "🧄", "🥔", "🍕", "🍔", "🍟", "🌭", "🍿",
            "🥪", "🌮", "🌯", "🥗", "🍜", "🍝", "🍣", "🍱", "🍩", "🍪"
    )),
    
    SPORTS("Sporlar", Arrays.asList(
            "⚽", "🏀", "🏈", "⚾", "🥎", "🎾", "🏐", "🏉", "🥏", "🎱",
            "🪀", "🏓", "🏸", "🏒", "🏑", "🥍", "🏏", "🪃", "🥅", "⛳",
            "🪁", "🏹", "🎣", "🤿", "🥊", "🥋", "🎽", "🛹", "🛼", "🛷",
            "⛸️", "🥌", "🎿", "⛷️", "🏂", "🪂", "🏋️", "🤸", "🏊", "🚴"
    )),
    
    FLAGS("Bayraklar", Arrays.asList(
            "🇹🇷", "🇺🇸", "🇬🇧", "🇩🇪", "🇫🇷", "🇮🇹", "🇪🇸", "🇯🇵", "🇨🇳", "🇰🇷",
            "🇧🇷", "🇷🇺", "🇮🇳", "🇦🇺", "🇨🇦", "🇲🇽", "🇦🇷", "🇳🇱", "🇧🇪", "🇸🇪",
            "🇳🇴", "🇩🇰", "🇫🇮", "🇵🇱", "🇬🇷", "🇵🇹", "🇨🇭", "🇦🇹", "🇮🇪", "🇿🇦",
            "🇪🇬", "🇸🇦", "🇦🇪", "🇮🇱", "🇹🇭", "🇻🇳", "🇮🇩", "🇲🇾", "🇸🇬", "🇵🇭"
    ));
    
    private final String displayName;
    private final List<String> contents;
    
    Theme(String displayName, List<String> contents) {
        this.displayName = displayName;
        this.contents = contents;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public List<String> getContents() {
        return contents;
    }
    
    /**
     * Belirtilen sayıda rastgele içerik döndürür
     */
    public List<String> getRandomContents(int count) {
        java.util.Collections.shuffle(contents);
        return contents.subList(0, Math.min(count, contents.size()));
    }
}
