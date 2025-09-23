// Pre-authentication script - runs immediately to handle page reloads
(function () {
  "use strict";

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

    // If URL already has token, clear stale user info so UI refreshes
    if (hasTokenInUrl()) {
      localStorage.removeItem("userName");
      localStorage.removeItem("userEmail");
      localStorage.removeItem("userId");
      return;
    }

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
