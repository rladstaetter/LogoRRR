# https://medium.com/@andrew.perfiliev/how-to-verify-app-signatures-43fd5cd1bd3d

# verify .app
codesign -v -v target/temporary-app-image/LogoRRR.app

# this command should return:
# target/temporary-app-image/LogoRRR.app: valid on disk
# target/temporary-app-image/LogoRRR.app: satisfies its Designated Requirement

# Another way to verify:
# see https://stackoverflow.com/questions/26067694/code-has-no-resources-but-signature-indicates-they-must-be-present
# spctl -at exec -vv target/temporary-app-image/LogoRRR.app/

# validate notarization
xcrun stapler validate target/unsigned-installer/LogoRRR-21.4.0.dmg