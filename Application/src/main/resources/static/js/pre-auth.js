// Pre-authentication script - runs immediately to handle page reloads
(function () {
  "use strict";

  // Read token from URL if present, store it, clear stale user data, and fetch fresh profile
  (function handleUrlToken() {
    const urlParams = new URLSearchParams(window.location.search);
    const urlToken = urlParams.get("token");
    if (!urlToken) return;

    // Clear stale user info so sidebar refreshes
    localStorage.removeItem("userName");
    localStorage.removeItem("userEmail");
    localStorage.removeItem("userId");

    // Store token in both local and session for compatibility
    localStorage.setItem("authToken", urlToken);
    localStorage.setItem("jwtToken", urlToken);
    sessionStorage.setItem("authToken", urlToken);

    // Clean URL by removing token param but keep others
    try {
      const url = new URL(window.location);
      url.searchParams.delete("token");
      window.history.replaceState({}, document.title, url.toString());
    } catch (e) {}

    // Fetch fresh user profile to populate sidebar immediately
    try {
      fetch("http://localhost:8083/api/auth/validate", {
        method: "POST",
        headers: { Authorization: `Bearer ${urlToken}` },
      })
        .then((r) => r.json())
        .then((data) => {
          if (data && data.valid) {
            if (data.name) localStorage.setItem("userName", data.name);
            if (data.email) localStorage.setItem("userEmail", data.email);
            if (data.userId)
              localStorage.setItem("userId", String(data.userId));
          }
        })
        .catch(() => {});
    } catch (e) {}
  })();

  // Function to get token from localStorage
  function getStoredToken() {
    return (
      localStorage.getItem("authToken") ||
      localStorage.getItem("jwtToken") ||
      sessionStorage.getItem("authToken") ||
      sessionStorage.getItem("jwtToken")
    );
  }

  // Function to check if current URL has token parameter
  function hasTokenInUrl() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.has("token");
  }

  // Function to check if this is a protected page
  function isProtectedPage() {
    const path = window.location.pathname;
    const protectedPaths = [
      "/dashboard",
      "/browse-jobs",
      "/my-applications",
      "/profile",
      "/job-details",
    ];
    return protectedPaths.some((protectedPath) => path.includes(protectedPath));
  }

  // Main pre-auth logic
  function handlePreAuth() {
    // Only run on protected pages
    if (!isProtectedPage()) {
      return;
    }

    // If URL already has token, we handled it above; nothing else to do here
    if (hasTokenInUrl()) return;

    // Check if we have a stored token
    const storedToken = getStoredToken();
    if (storedToken) {
      console.log("Page reload detected - redirecting with stored token");
      // Redirect to the same page but with token parameter
      const currentUrl = new URL(window.location);
      currentUrl.searchParams.set("token", storedToken);
      window.location.replace(currentUrl.toString());
      return;
    }

    // No token found anywhere - will be handled by server redirect to login
    console.log("No token found for protected page");
  }

  // Run immediately when script loads
  handlePreAuth();
})();
