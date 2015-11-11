var plist = require('plist');
var fs = require('fs');
var path = require('path');

// Add custom fields to Info.plist
var plistPath = path.join(__dirname, '../dist/MarkRight-darwin-x64/MarkRight.app/Contents', 'Info.plist');
var appPlist = plist.parse(fs.readFileSync(plistPath).toString());

appPlist.CFBundleDocumentTypes = [{
  CFBundleTypeExtensions: ["md", "markdown", ""],
  CFBundleTypeName: "Markdown",
  CFBundleTypeOSTypes: ["***"],
  CFBundleTypeRole: "Editor"
}];
fs.writeFileSync(plistPath, plist.build(appPlist));
