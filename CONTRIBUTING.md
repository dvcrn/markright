# Contributing guidelines

## General

1. Branch off of `develop`! PRs to `master` will get closed without consideration. 
2. Before opening a PR, make sure that you rebased the latest master and that your PR can get merged right away.
3. Make sure shadow-build is not complaining about any warnings / errors related to your change.

## Styleguide
- Use the dot syntax for javascript interop

```clj
;; Good
(.hello js/window)

;; Bad
(. js/window (hello))
```

- Use `:as` in `:require`. Avoid `:use`. 

```clj
;; good
(ns examples.ns
  (:require [clojure.zip :as zip]))

;; good
(ns examples.ns
  (:require [clojure.zip :refer [lefts rights]))

;; acceptable as warranted
(ns examples.ns
  (:require [clojure.zip :refer :all]))

;; bad
(ns examples.ns
  (:use clojure.zip))
```

_more to come_