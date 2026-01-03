// Shared Authentication Helper for Job Portal
// Include this in all HTML templates for seamless authentication

(function () {
  "use strict";

  // Authentication utility functions
  window.AuthHelper = {
    // Get JWT token from localStorage
    getToken: function () {
      return (
        localStorage.getItem("authToken") ||
        localStorage.getItem("jwtToken") ||
        sessionStorage.getItem("authToken") ||
        sessionStorage.getItem("jwtToken")
      );
    },

    // Set JWT token
    setToken: function (token) {
      console.log("Setting auth token:", token ? "Token received" : "No token");
      localStorage.setItem("authToken", token);
      localStorage.setItem("jwtToken", token); // Keep for compatibility
      // Note: We intentionally avoid cookie checks for localhost (different ports won't share cookies)
    },

    // Remove JWT token
    removeToken: function () {
      localStorage.removeItem("authToken");
      localStorage.removeItem("jwtToken");
      sessionStorage.removeItem("authToken");
      sessionStorage.removeItem("jwtToken");
      // Avoid cookie reliance in localhost multi-port setup
    },

    // Check if user is authenticated
    isAuthenticated: function () {
      const token = this.getToken();
      if (!token || token.length === 0) {
        return false;
      }

      // Basic token format validation (JWT should have 3 parts separated by dots)
      const parts = token.split(".");
      if (parts.length !== 3) {
        console.log("Invalid token format, removing token");
        this.removeToken();
        return false;
      }

      // Check if token is expired (basic check)
      try {
        const payload = JSON.parse(atob(parts[1]));
        const now = Math.floor(Date.now() / 1000);
        if (payload.exp && payload.exp < now) {
          console.log("Token expired, removing token");
          this.removeToken();
          return false;
        }
        return true;
      } catch (e) {
        console.log("Error parsing token, removing token");
        this.removeToken();
        return false;
      }
    },

    // Get auth headers for API calls
    getAuthHeaders: function () {
      const token = this.getToken();
      return token ? { Authorization: `Bearer ${token}` } : {};
    },

    // Redirect to login if not authenticated
    requireAuth: function () {
      // First check if there's a token in the URL and handle it
      this.handleTokenFromUrl();

      // Now check if authenticated
      if (!this.isAuthenticated()) {
        console.log("No valid token found, redirecting to login");
        // Give a small delay to ensure any async token processing is complete
        setTimeout(() => {
          if (!this.isAuthenticated()) {
            const authUrl = window.location.hostname === 'localhost' ? 'http://localhost:8083' : 'https://job-application-service-production.up.railway.app';
            window.location.href = authUrl + "/login";
          }
        }, 50);
        return false;
      }
      console.log("Authentication check passed");
      return true;
    },

    // Handle navigation with authentication
    navigateWithAuth: function (url) {
      console.log("AuthHelper.navigateWithAuth called with:", url);

      if (this.isAuthenticated()) {
        const token = this.getToken();
        console.log("Token found, navigating with token");
        // Check if URL already has parameters
        const separator = url.includes("?") ? "&" : "?";
        const fullUrl = `${url}${separator}token=${encodeURIComponent(token)}`;
        console.log("Navigating to:", fullUrl);
        window.location.href = fullUrl;
      } else {
        console.log("No token found, redirecting to login");
        window.location.href = "http://localhost:8083/login";
      }
    },

    // Setup automatic token injection for page navigation
    setupAuthInterceptor: function () {
      // Override click events for navigation links
      document.addEventListener("DOMContentLoaded", function () {
        // Intercept all navigation links
        document.addEventListener("click", function (e) {
          const link = e.target.closest("a");
          if (link && link.href) {
            const url = new URL(link.href);

            // Check if it's an internal navigation (dashboard, profile, etc.)
            const internalPaths = [
              "/dashboard",
              "/profile",
              "/browse-jobs",
              "/my-applications",
              "/create-job",
              "/job-details",
              "/job-listings",
            ];

            if (internalPaths.some((path) => url.pathname.includes(path))) {
              e.preventDefault();
              window.AuthHelper.navigateWithAuth(link.href);
            }
          }
        });

        // Handle form submissions with auth headers
        document.addEventListener("submit", function (e) {
          const form = e.target;
          if (form.method && form.method.toLowerCase() !== "get") {
            // Add token to form if authenticated
            if (window.AuthHelper.isAuthenticated()) {
              const token = window.AuthHelper.getToken();
              const tokenInput = document.createElement("input");
              tokenInput.type = "hidden";
              tokenInput.name = "token";
              tokenInput.value = token;
              form.appendChild(tokenInput);
            }
          }
        });
      });
    },

    // Extract token from URL parameters and store it
    handleTokenFromUrl: function () {
      const urlParams = new URLSearchParams(window.location.search);
      const urlToken = urlParams.get("token");

      if (urlToken) {
        this.setToken(urlToken);
        // Clean URL by removing token parameter
        urlParams.delete("token");
        const newUrl =
          window.location.pathname +
          (urlParams.toString() ? "?" + urlParams.toString() : "");
        window.history.replaceState({}, "", newUrl);
      }
    },

    // Initialize authentication helper
    init: function () {
      // Always handle tokens from URL first
      this.handleTokenFromUrl();

      // Add some debugging
      const token = this.getToken();
      if (token) {
        console.log(
          "AuthHelper initialized with token:",
          token.substring(0, 20) + "..."
        );
      } else {
        console.log("AuthHelper initialized without token");
      }
    },
  };

  // Legacy compatibility functions (used by existing templates)
  window.getJwtToken = function () {
    return window.AuthHelper.getToken();
  };

  window.getAuthHeaders = function () {
    return window.AuthHelper.getAuthHeaders();
  };

  window.checkAuth = function () {
    // For page reloads, check localStorage immediately
    const token =
      localStorage.getItem("authToken") || localStorage.getItem("jwtToken");
    if (token) {
      console.log("Found token in localStorage during page reload");
      return true;
    }

    // Otherwise use the standard requireAuth which handles URL tokens
    return window.AuthHelper.requireAuth();
  };

  // Auto-initialize when script loads
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", function () {
      window.AuthHelper.init();
    });
  } else {
    window.AuthHelper.init();
  }
})();
