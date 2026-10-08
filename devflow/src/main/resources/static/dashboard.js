
const STREAM_URL = "/api/agent-events/stream";

const timeline = document.getElementById("timeline");
const emptyState = document.getElementById("empty-state");

const eventCount = document.getElementById("event-count");
const activeAgent = document.getElementById("active-agent");
const lastEvent = document.getElementById("last-event");

const currentAgent = document.getElementById("current-agent");
const currentMessage = document.getElementById("current-message");

const connectionIndicator =
    document.getElementById("connection-indicator");

const connectionStatus =
    document.getElementById("connection-status");

let totalEvents = 0;


/* =========================
   SSE CONNECTION
========================= */

const eventSource = new EventSource(STREAM_URL);


/* Connected */

eventSource.onopen = () => {

    connectionIndicator.classList.add("connected");

    connectionStatus.textContent = "Connected";
};


/* Event received */

eventSource.addEventListener("agent-event", (event) => {

    const data = JSON.parse(event.data);

    handleAgentEvent(data);

});


/* Connection error */

eventSource.onerror = () => {

    connectionIndicator.classList.remove("connected");

    connectionStatus.textContent = "Disconnected";
};


/* =========================
   HANDLE EVENT
========================= */

function handleAgentEvent(event) {

    totalEvents++;

    eventCount.textContent = totalEvents;

    const agent = event.agent || "Unknown";
    const type = event.type || "activity";
    const message = event.message || "";

    const timestamp =
        event.timestamp
            ? new Date(event.timestamp)
            : new Date();


    /*
     * Update current activity
     */

    currentAgent.textContent = formatAgentName(agent);

    currentMessage.textContent = message;

    activeAgent.textContent =
        formatAgentName(agent);


    /*
     * Update timestamp
     */

    lastEvent.textContent =
        timestamp.toLocaleTimeString([], {
            hour: "2-digit",
            minute: "2-digit",
            second: "2-digit"
        });


    /*
     * Update sidebar status
     */

    updateAgentStatus(agent, message);


    /*
     * Add timeline event
     */

    addTimelineEvent(
        agent,
        type,
        message,
        timestamp
    );
}


/* =========================
   ADD TIMELINE EVENT
========================= */

function addTimelineEvent(
    agent,
    type,
    message,
    timestamp
) {

    if (emptyState) {
        emptyState.remove();
    }

    const eventElement =
        document.createElement("div");

    const agentClass =
        getAgentClass(agent);

    eventElement.className =
        `event ${agentClass}`;


    eventElement.innerHTML = `

        <div class="event-marker">
            ${getAgentInitials(agent)}
        </div>

        <div class="event-content">

            <div class="event-top">

                <span class="event-agent">
                    ${escapeHtml(formatAgentName(agent))}
                </span>

                <span class="event-type">
                    ${escapeHtml(type)}
                </span>

            </div>

            <div class="event-message">
                ${escapeHtml(message)}
            </div>

        </div>

        <div class="event-time">
            ${timestamp.toLocaleTimeString([], {
                hour: "2-digit",
                minute: "2-digit",
                second: "2-digit"
            })}
        </div>
    `;


    timeline.appendChild(eventElement);


    /*
     * Keep newest event visible
     */

    timeline.scrollTop =
        timeline.scrollHeight;
}


/* =========================
   AGENT STATUS
========================= */

function updateAgentStatus(agent, message) {

    const normalized =
        agent.toLowerCase().replace(/\s+/g, "");


    let statusElement =
        document.getElementById(
            `${normalized}-status`
        );

    let dotElement =
        document.getElementById(
            `${normalized}-dot`
        );


    if (!statusElement || !dotElement) {
        return;
    }


    statusElement.textContent =
        shortenStatus(message);

    dotElement.classList.add("active");


    /*
     * Reset other agents to idle
     */

    const agents = [
        "ba",
        "pm",
        "worker1"
    ];

    agents.forEach(name => {

        if (name !== normalized) {

            const status =
                document.getElementById(
                    `${name}-status`
                );

            const dot =
                document.getElementById(
                    `${name}-dot`
                );

            if (status) {
                status.textContent = "Idle";
            }

            if (dot) {
                dot.classList.remove("active");
            }
        }

    });
}


/* =========================
   HELPERS
========================= */

function formatAgentName(agent) {

    const name =
        agent.toLowerCase();

    if (name === "ba" ||
        name === "business analyst") {

        return "Business Analyst";
    }

    if (name === "pm" ||
        name === "project manager") {

        return "Project Manager";
    }

    if (name === "worker1" ||
        name === "worker 1") {

        return "Worker 1";
    }

    return agent;
}


function getAgentClass(agent) {

    const name =
        agent.toLowerCase();

    if (
        name === "ba" ||
        name === "business analyst"
    ) {
        return "ba";
    }

    if (
        name === "pm" ||
        name === "project manager"
    ) {
        return "pm";
    }

    return "worker";
}


function getAgentInitials(agent) {

    const name =
        agent.toLowerCase();

    if (name === "ba") {
        return "BA";
    }

    if (name === "pm") {
        return "PM";
    }

    if (name === "worker1") {
        return "W1";
    }

    return "AI";
}


function shortenStatus(message) {

    if (!message) {
        return "Working";
    }

    if (message.length <= 24) {
        return message;
    }

    return message.substring(0, 24) + "...";
}


function escapeHtml(value) {

    const div =
        document.createElement("div");

    div.textContent = value;

    return div.innerHTML;
}


/* =========================
   CLEAR EVENTS
========================= */

document
    .getElementById("clear-events")
    .addEventListener("click", () => {

        timeline.innerHTML = "";

        totalEvents = 0;

        eventCount.textContent = "0";

        const empty =
            document.createElement("div");

        empty.className = "empty-state";

        empty.innerHTML = `
            <div class="empty-icon">◌</div>
            <h3>Waiting for activity</h3>
            <p>
                Agent activity will appear here in real time.
            </p>
        `;

        timeline.appendChild(empty);
    });