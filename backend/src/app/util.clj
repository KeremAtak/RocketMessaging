(ns app.util)

(defn ->long-safe [x]
  (if (string? x)
    (try (Long/parseLong x)
         (catch NumberFormatException _ nil))
    x))