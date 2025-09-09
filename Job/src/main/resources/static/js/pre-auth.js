// Pre-authentication script for page reload handling
// This script runs immediately when the page loads to handle authentication
// BEFORE the page renders to avoid flicker and redirects

(function () {
  "use strict";

  console.log("Pre-auth script executing...");

  // Get token from URL parameter if present
  const urlParams = new URLSearchParams(window.location.search);
  const urlToken = urlParams.get("token");

  if (urlToken) {
    console.log("Token found in URL parameter, storing...");
    localStorage.setItem("authToken", urlToken);
    localStorage.setItem("jwtToken", urlToken); // Keep for compatibility

    // Clean URL by removing token parameter but keep other params
    const url = new URL(window.location);
    url.searchParams.delete("token");
    window.history.replaceState({}, document.title, url.toString());

    console.log("Token stored and URL cleaned");
  }

  // Check if we have a token stored
  const token =
    localStorage.getItem("authToken") ||
    localStorage.getItem("jwtToken") ||
    sessionStorage.getItem("authToken");

  if (!token) {
    console.log("No authentication token found, will redirect to login");
    // Don't redirect immediately - let the main auth logic handle it
    // This allows for proper server-side cookie checking
  } else {
    console.log("Authentication token found in storage");
  }
})();
