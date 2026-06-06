Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Import-DotEnv {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    if (-not (Test-Path $Path)) {
        return
    }

    Get-Content $Path | ForEach-Object {
        $line = $_.Trim()
        if ([string]::IsNullOrWhiteSpace($line) -or $line.StartsWith('#')) {
            return
        }

        $parts = $line -split '=', 2
        if ($parts.Count -ne 2) {
            return
        }

        $key = $parts[0].Trim()
        $value = $parts[1].Trim()
        [Environment]::SetEnvironmentVariable($key, $value, 'Process')
    }
}

$repoRoot = Split-Path -Parent $PSCommandPath
Push-Location $repoRoot

try {
    Import-DotEnv -Path (Join-Path $repoRoot '.env')

    if (-not $env:JWT_SECRET_PROD) {
        throw 'JWT_SECRET_PROD no esta definido. Configuralo en .env o en variables de entorno.'
    }

    docker compose -p kata-customers down --remove-orphans
    docker compose -p kata-customers --profile prod up --build -d
    docker compose -p kata-customers ps
}
finally {
    Pop-Location
}
