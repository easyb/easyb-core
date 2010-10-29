

scenario "a plugin missing should throw a RuntimeException", {
  then "throws", {
    ensureThrows(RuntimeException) {
      using 'wibble'
    }
  }
}