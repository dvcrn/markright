![banner](https://raw.githubusercontent.com/dvcrn/dmedit/master/resources/markright-banner.png)

a minimalistic github flavored markdown editor

![screenshot](https://raw.githubusercontent.com/dvcrn/markright/master/resources/screenshot.png)

## Download

Check out the [latest release][1] to quickly find the latest version. 
Here are the current binaries: 

- [Mac][2]
- [Windows 32bit][3]
- [Windows 64bit][4]

_(wanna contribute a automatic linux build?)_

## Building

MarkRight is written in clojurescript. To build, make sure you have clojure and leiningen installed on your system. 

### Requirements

- `npm install`
- `bower install`

### Compiling
All commands you need are available inside `package.json`. To compile the code, run `npm run compile:<prod/dev>`. `app/` is the folder that goes into electron.

`lein figwheel frontend:dev` is available for superior auto reloading of the react frontend. For the backend, `lein cljsbuild auto` should be enough. 


[1]: https://github.com/dvcrn/markright/releases/latest/
[2]: https://github.com/dvcrn/markright/releases/download/0.1.1/MarkRight_Mac.dmg
[3]: https://github.com/dvcrn/markright/releases/download/0.1.1/MarkRight_Windows32.exe
[4]: https://github.com/dvcrn/markright/releases/download/0.1.1/MarkRight_Windows64.exe