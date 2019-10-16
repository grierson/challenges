(ns alphabet-cipher.coder)


;; -- Alphabet from A to Z
(def atoz (map char "abcdefghijklmnopqrstuvwxyz"))



(defn value [alphabet letter]
  "Get index of letter in a-z"
  (.indexOf alphabet letter))


(defn gen-alpha
  "Generate alphabet starting from letter"
  [letter]
  (let [column (value atoz letter)
        start (range (+ column 26))
        cyclealpha #(char (+ 97 (rem % 26)))]
    (->> start
        (map cyclealpha)
        (drop column))))


;; -- Encode
(defn get-cipher
  "Keyword alphabet column + Message index in a-z = encoded char"
  [keyword message]
  (let [col (gen-alpha keyword)
        row (value atoz message)]
    (nth col row)))


(defn encode
  "Apply conversion to entire message"
  [keyword message]
  (let [cycledkeyword (take (count message) (cycle keyword))
        encodedmessage (map get-cipher cycledkeyword message)]
    (apply str encodedmessage)))


;; -- Decode
(defn get-message
  "Decode message given keyword and ciphered text"
  [keyword cipher]
  (let [coll (gen-alpha keyword)
        row (.indexOf coll cipher)]
    (nth atoz row)))


(defn decode
  "Apply revertion to entire message"
  [keyword cipher]
  (let [msgcipher (take (count cipher) (cycle keyword))
        decodedmessage (map get-message msgcipher cipher)]
    (apply str decodedmessage)))


;; -- Decipher
(defn get-keyword
  [cipher message]
  (let [alphabet (gen-alpha message)
        row (.indexOf alphabet cipher)]
    (nth atoz row)))

(defn find-word [s]
  (let [head (first s)
        div (quot (count s) (get (frequencies s) head))]
    (apply str (take div s))))

(defn decipher
  [cipher message]
  (find-word (apply str (map get-keyword cipher message))))

;; -- Find word in repeating
(decipher "opkyfipmfmwcvqoklyhxywgeecpvhelzg" "thequickbrownfoxjumpsoveralazydog")
(decipher "hcqxqqtqljmlzhwiivgbsapaiwcenmyu" "packmyboxwithfivedozenliquorjugs")
