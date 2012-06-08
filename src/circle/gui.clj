(ns circle.gui
  (:require [circle.dispatch :as dispatch])
  (:import (java.awt FileDialog)
           (java.awt.event KeyEvent)
           (java.io FilenameFilter)))

(defn set-frame [f]
  (def frame f))

(def file-filter (proxy [FilenameFilter] []
              (accept [_ f]
                (.endsWith f ".clj"))))

(defn load-source-file [_]
    (let [jfc (FileDialog. frame "Load..." FileDialog/LOAD)]
    (.setFilenameFilter jfc file-filter)
    (.setVisible jfc true)
    (let [result (.getFile jfc)
          file-dir (.getDirectory jfc)
          load-src (str file-dir result)]
      (when result
        (dispatch/fire :file-load-buffer load-src)))))

(defn key-event [{key :key modifier :modifier event :event}]
  (println (class key) key)
  (when (and (= key \l)
             (= modifier KeyEvent/META_MASK))
    (load-source-file nil)
    (.consume event)))
