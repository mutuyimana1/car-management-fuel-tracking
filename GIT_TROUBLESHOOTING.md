# Git Connection Troubleshooting Guide

## Error: "Recv failure: Connection was reset"

This error typically occurs due to network connectivity issues. Here are solutions:

## Solutions to Try

### 1. **Check Repository Access**
- Verify the repository exists: https://github.com/mutuyimana1/car-management-fuel-tracking
- Check if it's private (you'll need authentication)
- Ensure you have push access

### 2. **Use SSH Instead of HTTPS**
If HTTPS keeps failing, switch to SSH:

```bash
# Remove HTTPS remote
git remote remove origin

# Add SSH remote
git remote add origin git@github.com:mutuyimana1/car-management-fuel-tracking.git

# Test connection
git ls-remote origin
```

**Note:** You'll need to set up SSH keys first:
- Generate SSH key: `ssh-keygen -t ed25519 -C "your_email@example.com"`
- Add to GitHub: Settings → SSH and GPG keys → New SSH key

### 3. **Use Personal Access Token (HTTPS)**
GitHub no longer accepts passwords for HTTPS. Use a Personal Access Token:

1. Go to GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Generate new token with `repo` permissions
3. Use token as password when pushing:
   ```bash
   git push origin main
   # Username: your_username
   # Password: your_personal_access_token
   ```

### 4. **Check Network/Firewall**
- Disable VPN temporarily
- Check if corporate firewall is blocking GitHub
- Try from a different network

### 5. **Increase Timeout Settings**
```bash
git config --global http.postBuffer 524288000
git config --global http.lowSpeedLimit 0
git config --global http.lowSpeedTime 999999
```

### 6. **Use GitHub CLI**
Install GitHub CLI and authenticate:
```bash
# Install GitHub CLI
# Then authenticate
gh auth login

# Push using GitHub CLI
gh repo create car-management-fuel-tracking --public --source=. --remote=origin --push
```

### 7. **Manual Upload**
If all else fails:
1. Create a ZIP of your project
2. Go to GitHub repository
3. Upload files manually through the web interface

## Current Configuration

Your current remote:
```
origin  https://github.com/mutuyimana1/car-management-fuel-tracking.git
```

## Quick Test Commands

```bash
# Test if you can reach GitHub
ping github.com

# Test repository access
curl -I https://github.com/mutuyimana1/car-management-fuel-tracking

# Check Git configuration
git config --list | grep http
```

## Recommended Solution

**Best approach:** Use SSH or Personal Access Token with HTTPS.

1. **For SSH:** Set up SSH keys (most secure, no password needed)
2. **For HTTPS:** Use Personal Access Token (easier setup)

Choose the method that works best for your network environment.

