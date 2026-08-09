"use strict";

const express = require("express");
const cors = require("cors");

const app = express();

const PORT = Number(process.env.PORT) || 3000;
const PEXELS_API_KEY = process.env.PEXELS_API_KEY;

app.use(cors({
  origin: "*",
  methods: ["GET", "OPTIONS"]
}));

app.use(express.json());

/* =========================================
   HOME / HEALTH
========================================= */

app.get("/", (req, res) => {
  res.json({
    success: true,
    service: "Pixora API",
    status: "online"
  });
});

app.get("/api/health", (req, res) => {
  res.json({
    success: true,
    status: "online",
    pexelsConfigured: Boolean(PEXELS_API_KEY)
  });
});


/* =========================================
   PEXELS REQUEST
========================================= */

async function pexelsRequest(url) {

  if (!PEXELS_API_KEY) {
    throw new Error("PEXELS_API_KEY is missing.");
  }

  const response = await fetch(url, {
    headers: {
      Authorization: PEXELS_API_KEY
    }
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(
      `Pexels API error ${response.status}: ${text}`
    );
  }

  return response.json();
}


/* =========================================
   PHOTOS
========================================= */

app.get("/api/photos", async (req, res) => {

  try {

    const page = Math.max(
      1,
      Number(req.query.page) || 1
    );

    const perPage = Math.min(
      30,
      Math.max(
        1,
        Number(req.query.per_page) || 20
      )
    );

    const query =
      String(req.query.query || "").trim();

    let url;

    if (query) {

      url =
        "https://api.pexels.com/v1/search" +
        `?query=${encodeURIComponent(query)}` +
        `&page=${page}` +
        `&per_page=${perPage}`;

    } else {

      url =
        "https://api.pexels.com/v1/curated" +
        `?page=${page}` +
        `&per_page=${perPage}`;

    }

    const data =
      await pexelsRequest(url);

    res.json({
      success: true,
      type: "photos",
      page,
      perPage,
      totalResults: data.total_results || 0,
      nextPage: data.next_page || null,
      photos: data.photos || []
    });

  } catch (error) {

    console.error("PHOTOS ERROR:", error);

    res.status(500).json({
      success: false,
      error: "Unable to load photos."
    });

  }

});


/* =========================================
   VIDEOS
========================================= */

app.get("/api/videos", async (req, res) => {

  try {

    const page = Math.max(
      1,
      Number(req.query.page) || 1
    );

    const perPage = Math.min(
      30,
      Math.max(
        1,
        Number(req.query.per_page) || 15
      )
    );

    const query =
      String(req.query.query || "").trim();

    let url =
      "https://api.pexels.com/videos/search" +
      `?query=${encodeURIComponent(
        query || "nature"
      )}` +
      `&page=${page}` +
      `&per_page=${perPage}`;

    const data =
      await pexelsRequest(url);

    res.json({
      success: true,
      type: "videos",
      page,
      perPage,
      totalResults: data.total_results || 0,
      nextPage: data.next_page || null,
      videos: data.videos || []
    });

  } catch (error) {

    console.error("VIDEOS ERROR:", error);

    res.status(500).json({
      success: false,
      error: "Unable to load videos."
    });

  }

});


/* =========================================
   SEARCH
========================================= */

app.get("/api/search", async (req, res) => {

  try {

    const query =
      String(req.query.q || "").trim();

    const type =
      String(req.query.type || "photo")
        .toLowerCase();

    const page = Math.max(
      1,
      Number(req.query.page) || 1
    );

    if (!query) {

      return res.status(400).json({
        success: false,
        error: "Search query is required."
      });

    }

    const perPage = 20;

    if (type === "video") {

      const url =
        "https://api.pexels.com/videos/search" +
        `?query=${encodeURIComponent(query)}` +
        `&page=${page}` +
        `&per_page=${perPage}`;

      const data =
        await pexelsRequest(url);

      return res.json({
        success: true,
        type: "videos",
        page,
        nextPage: data.next_page || null,
        videos: data.videos || []
      });

    }

    const url =
      "https://api.pexels.com/v1/search" +
      `?query=${encodeURIComponent(query)}` +
      `&page=${page}` +
      `&per_page=${perPage}`;

    const data =
      await pexelsRequest(url);

    return res.json({
      success: true,
      type: "photos",
      page,
      nextPage: data.next_page || null,
      photos: data.photos || []
    });

  } catch (error) {

    console.error("SEARCH ERROR:", error);

    res.status(500).json({
      success: false,
      error: "Unable to search Pexels."
    });

  }

});


/* =========================================
   404
========================================= */

app.use((req, res) => {

  res.status(404).json({
    success: false,
    error: "API endpoint not found."
  });

});


/* =========================================
   SERVER
========================================= */

app.listen(PORT, "0.0.0.0", () => {

  console.log(
    `Pixora API running on port ${PORT}`
  );

});
