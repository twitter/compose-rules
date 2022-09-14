# Releasing

1. Update the `VERSION_NAME` in `gradle.properties` to the release version (e.g. remove `-SNAPSHOT` from it).

2. Commit and push to main

   ```
   $ git commit -am "Bump version to X.Y.Z"
   $ git push
   ```
   This will trigger a GitHub Action workflow that will publish the artifacts to Maven Central, and publish them.
3. Go to [Releases](https://github.com/twitter/compose-rules/releases) and you'll see a draft release with all the PRs listed in the changelog.
   1. Make sure the release name and the tags associated to it match. If not, you make them match.
   2. Publish the draft of the release.

4. Update the `VERSION_NAME` in `gradle.properties` to the next SNAPSHOT version by adding `-SNAPSHOT` to the version.
5. Commit and push to main

   ```
   $ git commit -am "Bump version to X.Y.Z-SNAPSHOT"
   $ git push
   ```
6. You're done! 🎉
