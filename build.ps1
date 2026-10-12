param([switch]$RunTests, [string]$EvidencePath)
$ErrorActionPreference = 'Stop'
Push-Location -LiteralPath $PSScriptRoot
try {
  $ErrorActionPreference = 'Continue'
  $flatSettings = & java -XshowSettings:properties -version 2>&1 | ForEach-Object { $_.ToString() }
  $ErrorActionPreference = 'Stop'
  $flatHomeLine = $flatSettings | Where-Object { $_ -match '^\s*java\.home\s*=' } | Select-Object -First 1
  if (-not $flatHomeLine) { throw 'Se necesita JDK 17 o posterior.' }
  $flatJdk = ($flatHomeLine -replace '^\s*java\.home\s*=\s*', '').Trim()
  $flatJavac = Join-Path $flatJdk 'bin/javac.exe'
  $flatJar = Join-Path $flatJdk 'bin/jar.exe'
  $flatJava = Join-Path $flatJdk 'bin/java.exe'
  if (-not (Test-Path -LiteralPath $flatJavac)) { throw 'El Java disponible debe ser un JDK 17 o posterior, no solo un JRE.' }
  New-Item -ItemType Directory -Force -Path 'build/classes','dist' | Out-Null
  $flatSources = Get-ChildItem -LiteralPath '.' -Filter '*.java' | ForEach-Object FullName
  & $flatJavac --release 17 -encoding UTF-8 -d 'build/classes' $flatSources
  if ($LASTEXITCODE -ne 0) { throw 'La compilacion fallo.' }
  & $flatJar --create --file 'dist/cu6-comprar-producto.jar' --main-class 'edu.itson.cu6.Main' -C 'build/classes' .
  if ($LASTEXITCODE -ne 0) { throw 'No se pudo generar el JAR.' }
  Write-Host 'Compilacion correcta: dist/cu6-comprar-producto.jar'
  if ($RunTests) {
    & $flatJava '-Dfile.encoding=UTF-8' '-Dstdout.encoding=UTF-8' '-Djava.awt.headless=true' -ea -cp 'build/classes' 'edu.itson.cu6.ModelTests'
    if ($LASTEXITCODE -ne 0) { throw 'Fallaron las pruebas.' }
  }
  if ($EvidencePath) {
    & $flatJava '-Dfile.encoding=UTF-8' '-Dstdout.encoding=UTF-8' '-Djava.awt.headless=true' -cp 'build/classes' 'edu.itson.cu6.ScreenshotHarness' $EvidencePath
    if ($LASTEXITCODE -ne 0) { throw 'Fallo la demostracion Swing.' }
  }
} finally { Pop-Location }

