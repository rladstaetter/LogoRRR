# test script before submitting
flatpak run org.flatpak.Builder --force-clean --sandbox --user --install --install-deps-from=flathub --ccache --mirror-screenshots-url=https://dl.flathub.org/media/ --repo=repo builddir app.logorrr.LogoRRR.yml

# run the app
flatpak run app.logorrr.LogoRRR

# lint the manifest
flatpak run --command=flatpak-builder-lint org.flatpak.Builder manifest app.logorrr.LogoRRR.yml

# lint repo
flatpak run --command=flatpak-builder-lint org.flatpak.Builder repo repo
