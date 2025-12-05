_This project is currently frozen. If you want to help developing it, please feel free to [ping me](https://github.com/dvcrn)_

![banner](resources/markright-banner.png)

a minimalistic github flavored markdown editor

![screenshot](resources/screenshot.png)

## Download

On mac, install it through [cask][8]:
```
brew install --cask markright
```

Alternatively, check out the [latest release][1] to quickly find the latest version. 
Here are the current binaries: 

- [Mac][2]
- [Windows 32bit][3]
- [Windows 64bit][4]
- [Linux ia32][5]
- [Linux x64][6]

### Command line

On mac, you can launch markright from the command line with a little alias: 
```
alias markright="open -a /Applications/MarkRight.app"
markright README.md
```

## Building

MarkRight is written in clojurescript and now builds with `shadow-cljs`.

### Requirements

- `bun`
- `bun install`

### Compiling
- `bun run release` compiles both the electron main process and renderer via `shadow-cljs` (output goes into `node/app.js` and `node/ui/js/`).
- `bun start` launches electron using the compiled output (run `bun run release` at least once before starting).

### Development

- `bun run watch` runs `shadow-cljs watch main front` for live recompilation (keep it running).
- In another terminal, run `bun start` to launch electron against the watched build.

## License

Licensed under [GPLv3][7]

[1]: https://github.com/dvcrn/markright/releases/latest/
[2]: https://github.com/dvcrn/markright/releases/download/0.1.11/MarkRight_Mac.dmg
[3]: https://github.com/dvcrn/markright/releases/download/0.1.11/MarkRight_Windows32.exe
[4]: https://github.com/dvcrn/markright/releases/download/0.1.11/MarkRight_Windows64.exe
[5]: https://github.com/dvcrn/markright/releases/download/0.1.11/MarkRight_Linux_ia32.zip
[6]: https://github.com/dvcrn/markright/releases/download/0.1.11/MarkRight_Linux_x64.zip
[7]: http://www.gnu.org/licenses/gpl-3.0.txt
[8]: http://caskroom.io/
