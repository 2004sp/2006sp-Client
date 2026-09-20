package client;

import bootstrap.DefaultClientBootstrap;
import java.applet.AppletContext;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseWheelEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

public class Client extends GameShell {
   public static boolean customPlayerModelModeEnabled = true;
   public boolean graphicsEnabled = true;
   private static boolean itemSearchSpawnMode = false;
   public static DisplayMode fullscreenDisplayMode;
   public static boolean hideRoofs = false;
   public static boolean hdMinimap = false;
   public static boolean hdChatbox = false;
   public static boolean hdHealthBar = false;
   public static boolean hdModels = false;
   public static boolean use2007Models = false;
   public static boolean extendedRevisionEnabled = true;
   public static int extendedModelCacheStoreIndex = 5;
   public static int extendedAnimationCacheStoreIndex = 6;
   private static int npcIndexBitCount = 13;
   public static int cacheStoreCount = 8;
   public static int screenMode = 0;
   public static int clientWidth = 765;
   public static int clientHeight = 503;
   public static int uiScalePercent = 100;
   private static final int MIN_UI_SCALE_PERCENT = 50;
   private static final int MAX_UI_SCALE_PERCENT = 200;
   private static final int RESIZABLE_CHAT_UI_WIDTH = 520;
   private static final int RESIZABLE_CHAT_UI_HEIGHT = 165;
   private static final int RESIZABLE_TAB_UI_WIDTH = 241;
   private static final int RESIZABLE_TAB_UI_HEIGHT = 335;
   private static final int RESIZABLE_MINIMAP_UI_WIDTH = 241;
   private static final int RESIZABLE_MINIMAP_UI_HEIGHT = 177;
   public static int logoStyle = 0;
   public static boolean customSettingVisiblePlayerNames = false;
   public static String version = "v1.0";
   public static int cameraZoom = 600;
   private static int fixedWidth = 765;
   private static int fixedHeight = 503;
   public static int minimumWindowWidth = 800;
   public static int minimumWindowHeight = 600;
   private static int minimumResizableWidth = 1000;
   private static int minimumResizableHeight = 700;
   public static int[] keybinds = new int[13];
   private boolean resizableTabPanelVisible = true;
   private boolean chatMessagesVisible = true;
   private FogRenderer fogRenderer = new FogRenderer();
   private static boolean particleRenderingEnabled = true;
   public static boolean fogEnabled = true;
   public static boolean autoScreenshots = true;
   public static boolean autoLogin = false;
   public static boolean customSoundfontEnabled = false;
   public static String customSoundfontName = "";
   private boolean gameframeSelectionAllowed = true;
   private NumberFormat numberFormat = NumberFormat.getIntegerInstance(Locale.ENGLISH);
   private boolean unusedClientFlagA = false;
   private static boolean transparentTabArea = false;
   private boolean unusedClientFlagB = false;
   private boolean customSettingShowExperiencePerHour = false;
   public static boolean christmasEventActive = false;
   public static boolean halloweenEventActive = false;
   private TimeZone timeZone = TimeZone.getDefault();
   private int MODELS = 16777215;
   private int ANIMS = 0;
   public static int gameframeVersion = 317;
   public static boolean osrsResizableFrame = false;
   public static boolean showTitlebar = true;
   public static boolean orbsEnabled = false;
   private int MIDI = 400;
   private int WORLD = 321;
   private int cButtonHPos = 547;
   private int cButtonCPos = this.MIDI;
   private int setChannel = 0;
   public static boolean smoothRendering = true;
   public static boolean smoothAnimations = false;
   public static int attackOption = 0;
   public static boolean showAlchValueOnExamine = true;
   public static boolean showDamageType = true;
   public static boolean fishIconsEnabled = false;
   private static int[] fishingSpotNpcIds = new int[]{309, 312, 313, 316, 800, 1174};
   private static int[] fishingSpotItemIds = new int[]{335, 377, 383, 317, 2148, 7944};
   public static boolean wildernessLevelRangeEnabled = true;
   public static int wildernessLevelRangeColor = 16777215;
   public static boolean combatBoxEnabled = false;
   public static boolean skillBoxEnabled = true;
   public static int xpDropPosition = 1;
   public static int xpDropSize = 0;
   public static int xpDropColor = 16748608;
   static ArrayList groundItemOtherNames = new ArrayList();
   static ArrayList groundItemRareNames = new ArrayList();
   public static boolean groundItemOtherLootBeam = false;
   public static boolean groundItemRareLootBeam = true;
   public static boolean groundItemOtherNamesEnabled = false;
   public static boolean groundItemOtherMenuColorEnabled = false;
   public static int groundItemOtherMenuColor = 16711935;
   public static int groundItemOtherTextSize = 0;
   public static int groundItemOtherTextColor = 16777215;
   public static boolean groundItemRareNamesEnabled = false;
   public static boolean groundItemRareMenuColorEnabled = false;
   public static int groundItemRareMenuColor = 16711935;
   public static int groundItemRareTextSize = 0;
   public static int groundItemRareTextColor = 16777215;
   private int customSettingShowExperiencePerHourStartLevels = 0;
   private boolean customSettingVisualFixes = false;
   private int wideTabBarWidthThreshold = 1000;
   public static ArrayList extendedModelIds = new ArrayList();
   private int chatTypeView = -1;
   private int clanChatMode;
   private String[] itemSearchResultNames = new String[100];
   private int[] itemSearchResultIds = new int[100];
   private int autoCastId;
   public static Sprite[] customSprites;
   public static Sprite[] interfaceSprites;
   private static Client clientInstance;
   public static String serverAddress;
   private static int chatViewMode;
   private int ignoreCount;
   private long lastRegionLoadActivityMillis;
   private int[][] pathDistances;
   private int[] friendWorlds;
   private NodeDeque[][][] groundItems;
   private int[] flameIntensity;
   private int[] flameIntensityScratch;
   private volatile boolean flameThreadRunning;
   private Socket jaggrabSocket;
   private int loginScreenState;
   private Buffer chatBuffer;
   private Npc[] npcs;
   private int npcCount;
   private int[] npcIndices;
   private int removedEntityCount;
   private int[] removedEntityIndices;
   private int lastOpcode;
   private int prevPktType;
   private int prevPktType2;
   private String clickToContinueString;
   private int publicChatMode;
   private int privateChatMode;
   private Buffer loginBuffer;
   private static int systemUpdateNoiseCounter;
   private int[] activeFlamePalette;
   private int[] warmFlamePalette;
   private int[] greenFlamePalette;
   private int[] blueFlamePalette;
   private static int textureAnimationNoiseCounter;
   private int hintIconDrawType;
   public int openInterfaceId;
   private int castleWarsCatapultAimX = 15;
   private int castleWarsCatapultAimY = 15;
   private int cameraPositionX;
   private int cameraPositionZ;
   private int xCameraPos;
   private int zCameraPos;
   private int yCameraPos;
   private int yCameraCurve;
   private int xCameraCurve;
   private int myPrivilege = 0;
   private int[] currentExp;
   private IndexedSprite redStone1_3;
   private IndexedSprite redStone2_3;
   private IndexedSprite redStone3_2;
   private IndexedSprite redStone1_4;
   private IndexedSprite redStone2_4;
   private Sprite multiOverlay;
   private Sprite mapFlag;
   private Sprite mapMarker;
   private boolean archiveRetryToggle;
   private int[] cameraShakeRandomAmplitude;
   private boolean[] cameraShakeActive;
   private int weight;
   private MouseRecorder mouseRecorder;
   private volatile boolean drawFlames;
   private String reportAbuseInput;
   private int localPlayerIndex;
   private boolean menuOpen;
   private int hoveredWidgetId;
   private String inputString;
   private int maxPlayers;
   private static byte[] loadedMusicData;
   private int myPlayerIndex;
   private Player[] players;
   private int playerCount;
   private int[] playerIndices;
   private int entityUpdateCount;
   private int[] entityUpdateIndices;
   private Buffer[] playerAppearanceBuffers;
   private int cameraRotation;
   private int cameraRotationVelocity;
   private int friendCount;
   private int friendServerStatus;
   private int[][] pathDirections;
   private int unusedColor7759444;
   private BufferedImageGraphicsBuffer backVmidIP2_2;
   private byte[] animatedTextureScratch;
   private int interfaceContent206Mode;
   private int crossX;
   private int crossY;
   private int crossIndex;
   private int crossType;
   private int plane;
   private int[] currentStats;
   private static int objectActionNoiseCounter;
   private long[] ignoreListAsLongs;
   private boolean loadingError;
   private int unusedColor3353893;
   private int[] cameraShakeSineFrequency;
   private int[][] tileCycleMarkers;
   private Sprite femaleCharacterSprite;
   private Sprite maleCharacterSprite;
   private int hintIconPlayerId;
   private int hintIconX;
   private int hintIconY;
   private int hintIconHeight;
   private int hintIconOffsetX;
   private int hintIconOffsetY;
   private static int chatModeNoiseCounter;
   private int[] chatTypes;
   private String[] chatNames;
   private String[] chatMessages;
   private int[] chatPrivileges;
   private int[] chatDonatorStatuses;
   private int[] chatAccountModes;
   private int animationCycleDelta;
   private SceneGraph scene;
   private IndexedSprite[] sideIcons;
   private int menuScreenArea;
   private int menuOffsetX;
   private int menuOffsetY;
   private int menuWidth;
   private int menuHeight;
   private long privateMessageTarget;
   private boolean focusReported;
   private long[] friendEncodedNames;
   private int currentSong;
   private static int nodeID = 10;
   static int portOff;
   private static boolean isMembers = true;
   private static boolean lowMem;
   private volatile boolean drawingFlames;
   private int spriteDrawX;
   private int spriteDrawY;
   private int[] chatTextColors = new int[]{16776960, 16711680, 65280, 65535, 16711935, 16777215};
   private IndexedSprite titleBox;
   private IndexedSprite titleButton;
   private static int[] compassMaskLineOffsets;
   private int[] flameLineOffsets;
   final CacheStore[] cacheStores;
   public int[] varps;
   private static int selectedSpellHighlightWidgetId = 0;
   private boolean scrollbarDragging;
   private int overheadTextCapacity;
   private int[] overheadTextX;
   private int[] overheadTextY;
   private int[] overheadTextHeight;
   private int[] overheadTextHalfWidth;
   private int[] textColourEffect;
   private int[] overheadTextEffects;
   private int[] overheadTextCycles;
   private String[] overheadTexts;
   private int cameraDistanceScale;
   private int lastRenderedPlane;
   private static int playerActionNoiseCounter;
   private Sprite[] hitMarks;
   private int cameraJitterCounter;
   private int widgetDragDuration;
   private int[] characterDesignColours;
   private static boolean startupInitialized;
   private boolean unusedCharacterFlag;
   private int cameraTargetTileX;
   private int cameraTargetTileY;
   private int cameraTargetHeightOffset;
   private int cameraTargetMoveSpeed;
   private int cameraTargetMoveAcceleration;
   private IsaacCipher incomingIsaacCipher;
   private Sprite mapEdge;
   private int unusedColor2301979;
   static final int[][] playerBodyColors = new int[][]{
      {6798, 107, 10283, 16, 4797, 7744, 5799, 4634, 33697, 22433, 2983, 54193},
      {8741, 12, 64030, 43162, 7735, 8404, 1701, 38430, 24094, 10153, 56621, 4783, 1341, 16578, 35003, 25239},
      {25238, 8742, 12, 64030, 43162, 7735, 8404, 1701, 38430, 24094, 10153, 56621, 4783, 1341, 16578, 35003},
      {4626, 11146, 6439, 12, 4758, 10270},
      {4550, 4537, 5681, 5673, 5790, 6806, 8076, 4574}
   };
   private String amountOrNameInput;
   private static int cameraNoiseCounter;
   private int lastLoginDaysAgo;
   private int daysSinceLastLogin;
   private int pktSize;
   private int pktType;
   private int timeoutCounter;
   private int outboundFlushCounter;
   private int logoutTimer;
   private NodeDeque projectiles;
   private int cameraFocusX;
   private int cameraFocusY;
   private int cameraPacketCooldown;
   private boolean cameraPacketPending;
   private int openWalkableInterface;
   private static int[] experienceTable = new int[99];
   private int minimapState;
   private int duplicateClickCount;
   private int loadingStage;
   private IndexedSprite scrollBar1;
   private IndexedSprite scrollBar2;
   private int viewportHoverWidgetId;
   private IndexedSprite backBase1;
   private IndexedSprite backBase2;
   private IndexedSprite backHmid1;
   private int[] cameraShakePhase;
   private boolean characterDesignNeedsRebuild;
   private Sprite[] mapFunctions;
   public int baseX;
   public int baseY;
   private int previousBaseX;
   private int previousBaseY;
   private int loginFailures;
   private int chatHoverWidgetId;
   private int greenFlameTransition;
   private int blueFlameTransition;
   private int dialogID;
   private int[] maxStats;
   private int[] serverVarps;
   private int pendingCombatStyleValue = -1;
   private long pendingCombatStyleUntilMillis = 0L;
   private int member;
   private boolean maleCharacter;
   private int tabHoverWidgetId;
   private String loadingStatusText;
   private static int regionBuildNoiseCounter;
   private static int[] minimapMaskLineOffsets;
   private Archive titleArchive;
   private int flashingSidebarId;
   private int multicombat;
   private NodeDeque incompleteAnimables;
   private static int[] compassMaskLineWidths;
   private Widget autocastWidget;
   private IndexedSprite[] mapSceneSprites;
   private static int sceneDrawCounter;
   private int currentSound;
   private int barFillColor;
   private int friendsListAction;
   private int[] characterDesignKitIds;
   private int mouseInvInterfaceIndex;
   private int lastActiveInvInterface;
   public OnDemandFetcher onDemandFetcher;
   private int mapRegionX;
   private int mapRegionY;
   private int mapFunctionCount;
   private int[] minimapHintX;
   private int[] minimapHintY;
   private Sprite mapDotItem;
   private Sprite mapDotNpc;
   private Sprite mapDotNPC;
   private Sprite mapDotPlayer;
   private Sprite mapDotFriend;
   private Sprite mapDotTeam;
   private int loadingErrorCode;
   private boolean validLocalMap;
   private String[] friendNames;
   private Buffer inStream;
   private int draggedWidgetId;
   private int draggedSlot;
   private int activeInterfaceType;
   private int dragStartX;
   private int dragStartY;
   private int chatScrollOffset;
   private int[] archiveCrcs;
   private int[] menuParam0;
   private int[] menuParam1;
   private int[] menuActionIds;
   private int[] menuParam2;
   private Sprite[] headIcons;
   private Sprite[] skullIcons;
   private Sprite[] headIconsHint;
   public static Sprite[] miscInterfaceSprites;
   private static int regionLoadCounter;
   private int x;
   private int y;
   private int height;
   private int speed;
   private int angle;
   public boolean tabAreaAltered;
   private int systemUpdateTime;
   private BufferedImageGraphicsBuffer topLeft1BackgroundTile;
   private BufferedImageGraphicsBuffer bottomLeft1BackgroundTile;
   private BufferedImageGraphicsBuffer flameLeftBackground;
   private BufferedImageGraphicsBuffer flameRightBackground;
   private BufferedImageGraphicsBuffer bottomLeft0BackgroundTile;
   private BufferedImageGraphicsBuffer bottomRightImageProducer;
   private BufferedImageGraphicsBuffer loginMusicImageProducer;
   private BufferedImageGraphicsBuffer middleLeft1BackgroundTile;
   private BufferedImageGraphicsBuffer middleRightBackgroundBuffer;
   private static int minimapClickNoiseCounter;
   private int membersInt;
   private String promptMessage;
   private static Sprite compassSprite;
   private static Sprite defaultCompassSprite;
   private BufferedImageGraphicsBuffer bottomFrameStripBuffer;
   private BufferedImageGraphicsBuffer bottomRightFrameStripBuffer;
   private BufferedImageGraphicsBuffer topRightFrameStripBuffer;
   private BufferedImageGraphicsBuffer rightFrameStripBuffer;
   private BufferedImageGraphicsBuffer middleRightFrameStripBuffer;
   private BufferedImageGraphicsBuffer chatRightFrameStripBuffer;
   private BufferedImageGraphicsBuffer chatTopFrameStripBuffer;
   private BufferedImageGraphicsBuffer chatLeftFrameStripBuffer;
   private BufferedImageGraphicsBuffer minimapRightFrameStripBuffer;
   private BufferedImageGraphicsBuffer minimapLeftFrameStripBuffer;
   private BufferedImageGraphicsBuffer chatSettingImageProducer;
   public static Player localPlayer;
   private String[] atPlayerActions;
   private boolean[] atPlayerArray;
   private int[][][] instanceChunkTemplates;
   private int[] tabInterfaceIds = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
   private int cameraY;
   private int cameraYVelocity;
   private int menuActionCount;
   private static int keepaliveCounter;
   private int spellSelected;
   private int selectedSpellWidgetId;
   private int spellUsableOn;
   private String spellTooltip;
   private Sprite[] minimapHint;
   private boolean inPlayerOwnedHouse;
   private static int crossAnimationCounter;
   private IndexedSprite redStone1;
   private IndexedSprite redStone2;
   private IndexedSprite redStone3;
   private IndexedSprite redStone1_2;
   private IndexedSprite redStone2_2;
   private int energy;
   private boolean continuedDialogue;
   private Sprite[] crosses;
   private Sprite[] skillIconSprites;
   private IndexedSprite[] titleRuneSprites;
   public boolean needDrawTabArea;
   public static int midiMasterVolume = 256;
   public static int[] midiChannelVolumes = new int[]{12800, 12800, 12800, 12800, 12800, 12800, 12800, 12800, 12800, 12800, 12800, 12800, 12800, 12800, 12800, 12800};
   private static int musicFadeTicksRemaining = 0;
   private static MidiPlayer midiPlayer;
   private static boolean musicRequestPending = false;
   private static int requestedMusicVolume;
   private static int currentMusicVolume = -1;
   private static byte[] pendingMusicData;
   private static int musicFadePosition = 0;
   private static int musicFadeStep = 0;
   private static int pendingMusicVolume;
   private static boolean pendingMusicLoop;
   private static int musicFadeDuration;
   private static boolean requestedMusicLoop;
   private static int musicTransitionDelay;
   private static int musicVolumeSetting = 255;
   public static int audioSampleRate;
   private static AudioPlayerBase audioPlayer;
   public static long lastAudioUpdateMillis;
   private static Class audioPlayerClass;
   private static int pcmBacklogMicros;
   private static PcmStream pcmStream;
   private static int pcmTimingAccumulator;
   private static PcmStreamMixer pcmStreamMixer;
   private static int soundEffectVolume = 127;
   private static SoundEffect[] queuedSoundEffects = new SoundEffect[50];
   private static AudioResampler audioResampler;
   private int unreadMessages;
   private static int audioNoiseCounter;
   private static boolean fpsOn;
   public static boolean loggedIn;
   private boolean canMute;
   private boolean constructedViewport;
   private boolean oriented;
   static int gameCycle;
   private BufferedImageGraphicsBuffer tabImageProducer;
   private BufferedImageGraphicsBuffer minimapImageProducer;
   private BufferedImageGraphicsBuffer gameScreenImageProducer;
   private BufferedImageGraphicsBuffer chatboxImageProducer;
   private int daysSinceRecovChange;
   private BufferedConnection connection;
   private int minimapInt3;
   private int minimapRotationVelocity;
   public static String username;
   public static String password;
   private static int inventoryActionNoiseCounter;
   private boolean genericLoadingError;
   private int[] objectTypeSceneGroups = new int[]{0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3};
   private int reportAbuseInterfaceID;
   private NodeDeque spawns;
   private static int[] chatboxScanOffsets;
   private static int[] sidebarScanOffsets;
   private static int[] viewportScanOffsets;
   private byte[][] terrainRegionData;
   private int cameraPitch;
   private int minimapInt1;
   private int cameraYawVelocity;
   private int cameraPitchVelocity;
   private static int playerInteractionNoiseCounter;
   private int invOverlayInterfaceID;
   private int[] flameNoise;
   private int[] flameNoiseScratch;
   public Buffer outgoingBuffer;
   private int lastLoginIp;
   private int splitpublicChat;
   private IndexedSprite invBack;
   private static IndexedSprite mapBack;
   private static IndexedSprite[] mapBackSprites;
   private IndexedSprite chatBack;
   private String[] menuActionNames;
   private Sprite originalFlameRightBackground;
   private Sprite originalBottomLeftBackground;
   private int[] cameraShakeSineAmplitude;
   static final int[] secondaryPlayerBodyColors = new int[]{9104, 10275, 7595, 3610, 7975, 8526, 918, 38802, 24466, 10145, 58654, 5027, 1457, 16565, 34991, 25486};
   private static boolean flagged;
   private int[] sound;
   private int flameCycle;
   private int minimapInt2;
   private int minimapZoomVelocity;
   private int chatContentHeight;
   private String promptInput;
   private int scrollbarClickTicks;
   private int[][][] intGroundArray;
   private long serverSeed;
   private int loginScreenCursorPos;
   private IndexedSprite[] moderatorIcons;
   private IndexedSprite[] gameModeIcons;
   private long lastClickTime;
   public int currentTab;
   private int hintIconNpcId;
   private boolean inputTaken;
   private int inputDialogState;
   private static int npcInteractionNoiseCounter;
   private int nextSong;
   private static int[] minimapMaskLineWidths;
   private CollisionMap[] collisionMaps;
   public static int[] bitMasks;
   private boolean chatSettingsRedraw;
   private int[] regionIds;
   private int[] terrainArchiveIds;
   private int[] objectArchiveIds;
   private int lastSentMouseX;
   private int lastSentMouseY;
   private int[] soundType;
   private boolean widgetDragThresholdExceeded;
   private int atInventoryLoopCycle;
   private int atInventoryInterface;
   private int atInventoryIndex;
   private int atInventoryInterfaceType;
   private byte[][] objectRegionData;
   private int tradeMode;
   private int chatEffectsDisabled;
   private int[] soundVolume;
   private int onTutorialIsland;
   private boolean rsAlreadyLoaded;
   private int oneButtonMouse;
   private int minimapJitterCounter;
   private boolean welcomeScreenRaised;
   private boolean messagePromptRaised;
   private byte[][][] byteGroundArray;
   private int previousSong;
   private int destX;
   private int destY;
   private Sprite minimapImage;
   private int alternativeRouteUsed;
   private int sceneCycle;
   private String loginMessage1;
   private String loginMessage2;
   private int localX;
   private int localY;
   private BitmapFont smallFont;
   private BitmapFont plainFont;
   private BitmapFont boldFont;
   private RichTextFont richSmallFont;
   private RichTextFont richPlainFont;
   private RichTextFont richBoldFont;
   private RichTextFont richQuillFont;
   private Sprite[] chatIconSprites = new Sprite[7];
   private int flameNoiseOffset;
   private int backDialogID;
   private int cameraX;
   private int cameraJitterXVelocity;
   private int[] bigX;
   private int[] bigY;
   private int itemSelected;
   private int selectedItemSlot;
   private int selectedItemWidgetId;
   private int selectedItemId;
   private String selectedItemName;
   private static int movementNoiseCounter;
   private static int priorityMenuActionIndex = -1;
   private int interfaceRedrawCounter;
   private int fullscreenInterfaceId;
   private int tabTooltipWidgetId;
   private int viewportTooltipWidgetId;
   private int hoveredTooltipWidgetId;
   private int chatTooltipWidgetId;
   private int tooltipHoverTicks;
   private static int[] fullScreenScanOffsets;
   private int hoveredMenuActionIndex;
   private int hoveredChatMode;
   private static int tiara;
   private int projectedEntityX = 0;
   private int projectedEntityY = 0;
   private int tabBarBackgroundSpriteId = 88;
   private int tabBarSelectedSpriteId = 89;
   private static boolean useDesktopWindow = true;
   private static int selectedBankTab = 0;
   private static int bankTabSummaryWidgetId = 19531;
   private static int mainBankContainerWidgetId = 5382;
   private static BankTab[] bankTabs = new BankTab[]{
       new BankTab(0, mainBankContainerWidgetId, 19509, 19508, 0, 0, false),
       new BankTab(10, 19532, 19511, 19510, 0, 0, false),
       new BankTab(11, 19533, 19513, 19512, 0, 0, false),
       new BankTab(12, 19534, 19515, 19514, 0, 0, false),
       new BankTab(13, 19535, 19517, 19516, 0, 0, false),
       new BankTab(14, 19536, 19519, 19518, 0, 0, false),
       new BankTab(15, 19537, 19521, 19520, 0, 0, false),
       new BankTab(16, 19538, 19523, 19522, 0, 0, false),
       new BankTab(17, 19539, 19525, 19524, 0, 0, false),
       new BankTab(18, 19540, 19527, 19526, 0, 0, false)
   };
   private int lastViewportTooltipWidgetId = 0;
   private int tooltipDelayTicks = 50;
   BufferedImageGraphicsBuffer frameBuffer;
   private int[] uiChatBackground;
   private int[] uiChatComposite;
   private int[] uiTabBackground;
   private int[] uiTabComposite;
   private int[] uiMinimapBackground;
   private int[] uiMinimapComposite;
   private static int[][] bankTabItemIds;
   private int[][] bankTabItemAmounts;
   private String bankTitle = "";
   static String server = "";
   private int savedBankScrollPosition = 0;
   private int bankSearchLastMatchingTab = -1;
   private boolean bankSearchHasMatches = false;
   static boolean fetchMusic = false;
   private int itemSpawnAmount = 1;
   private int drawCount = 88;
   private static Sprite backRight2Sprite;
   private static Sprite backVmid2Sprite;
   private static Sprite backVmid3Sprite;
   private static Sprite backHmid2Sprite;
   private static Sprite backLeft2Sprite;
   private static Sprite backRight1Sprite;
   private static Sprite backVmid1Sprite;
   private static Sprite backTop1Sprite;
   private static int activeChatRasterWidth = 479;
   private static int activeChatRasterHeight = 96;
   private static int classicChatRasterWidth = 479;
   private static int classicChatRasterHeight = 96;
   private static int modernChatRasterWidth = 506;
   private static int modernChatRasterHeight = 131;
   private static int activeSidebarRasterWidth = 190;
   private static int musicVolume = 190;
   private static int modernSidebarRasterWidth = 190;
   private int loadedGameframeVersion = 317;
   private int autoScreenshotDelay = 0;
   private int lastScreenshotDialogId = -1;
   private int lastScreenshotInterfaceId = -1;
   private boolean deathScreenshotTaken = false;
   private boolean centeredWalkableInterface = false;
   private boolean poisoned = false;
   private boolean runEnabled = false;
   private boolean runOrbHovered = false;
   private int minimapDrawX = 25;
   private int minimapDrawY = 5;
   private boolean autoLoginAttempted = false;
   private boolean fullscreenInterfaceBackdropVisible = false;
   private boolean fullscreenInterfaceCoversViewport = false;
   private ArrayList particles;
   private ArrayList deadParticles;
   private boolean suppressNextMinimapClick = false;

   static {

      int cumulativeExperience = 0;

      for (int levelIndex = 0; levelIndex < 99; levelIndex++) {
         int level = levelIndex + 1;
         int levelExperience = (int)(level + 300.0 * Math.pow(2.0, level / 7.0));
         cumulativeExperience += levelExperience;
         experienceTable[levelIndex] = cumulativeExperience / 4;
      }

      bitMasks = new int[32];
      byte bitMask = 2;

      for (int bitIndex = 0; bitIndex < 32; bitIndex++) {
         bitMasks[bitIndex] = bitMask - 1;
         bitMask += bitMask;
      }
   }
   public static boolean isSequenceDecodeHookEnabled() {
      return false;
   }
   public static int getAnimationFrameArchiveId(int sequenceIndex) {
      return AnimationSequence.sequences[sequenceIndex].frameIds[0] >> 16;
   }
   public static boolean isCacheOnlyModelId(int id) {
      if (!hdModels && use2007Models && id == 2847) {
         return true;
      } else if (hdModels && id == 3325) {
         return true;
      } else if (id == 1497) {
         return true;
      } else if (id == 794) {
         return true;
      } else if (id == 6144) {
         return true;
      } else if (id == 634) {
         return true;
      } else if (id == 635) {
         return true;
      } else if (id == 574) {
         return true;
      } else if (id == 575) {
         return true;
      } else if (id == 576) {
         return true;
      } else if (id == 678) {
         return true;
      } else if (id == 679) {
         return true;
      } else if (id == 636) {
         return true;
      } else if (id == 637) {
         return true;
      } else if (id == 638) {
         return true;
      } else if (id == 1960) {
         return true;
      } else if (id == 1965) {
         return true;
      } else if (id == 1961) {
         return true;
      } else if (id == 1962) {
         return true;
      } else if (id == 1963) {
         return true;
      } else if (id == 1964) {
         return true;
      } else if (id == 3367) {
         return true;
      } else if (id == 3369) {
         return true;
      } else if (id == 5058) {
         return true;
      } else if (id == 5112) {
         return true;
      } else if (id == 2847) {
         return true;
      } else {
         return id == 9296 ? true : id > 14926 && id <= 15027;
      }
   }
   public static int[] flattenIntGrid(int[][] values) {
      int[] localLength = new int[values.length * values[0].length];

      for (int loopIndex = 0; loopIndex < values.length; loopIndex++) {
         for (int loopIndex2 = 0; loopIndex2 < values[loopIndex].length; loopIndex2++) {
            localLength[loopIndex * values[0].length + loopIndex2] = values[loopIndex][loopIndex2];
         }
      }

      return localLength;
   }
   public static int[][] reshapeIntArray(int[] values, int spriteWidth) {
      if (values == null) {
         return new int[1][1];
      }

      int[][] localLength = new int[values.length / spriteWidth][spriteWidth];

      for (int loopIndex = 0; loopIndex < values.length; loopIndex++) {
         localLength[loopIndex / spriteWidth][loopIndex % spriteWidth] = values[loopIndex];
      }

      return localLength;
   }
   private boolean isMouseInRectangle(int sourceCanvasHeight, int canvasHeight, int clientWidth, int clientHeight) {
      boolean flag = false;
      int sourceClientHeight = clientHeight;
      clientHeight = clientWidth;
      clientWidth = canvasHeight;
      canvasHeight = sourceCanvasHeight;
      Client client = this;
      int scalar = canvasHeight <= clientHeight ? canvasHeight : clientHeight;
      canvasHeight = canvasHeight <= clientHeight ? clientHeight : canvasHeight;
      clientHeight = clientWidth <= sourceClientHeight ? clientWidth : sourceClientHeight;
      clientWidth = clientWidth <= sourceClientHeight ? sourceClientHeight : clientWidth;
      return client.mouseX >= scalar && client.mouseX <= canvasHeight && client.mouseY >= clientHeight && client.mouseY <= clientWidth;
   }
   public static int getProjectionScaleShift() {
      int localClientWidth = 512;
      if (screenMode != 0) {
         localClientWidth = clientWidth;
      }

      return (int)(Math.log(localClientWidth) / Math.log(2.0));
   }
   private void updateClientWindowSize(boolean flag) {
      Frame gameFrame = super.gameFrame;
      byte byteCode = 8;
      byte byteCode2 = 54;
      if (ClientWindow.getInstance() != null) {
         gameFrame = ClientWindow.getInstance().frame;
         if (!ClientWindow.showTitleBar) {
            byteCode = 0;
            byteCode2 = 21;
         }
      }

      if (super.fullscreenActive) {
         super.restoreWindowedMode();
      }

      int sourceClientWidth = clientWidth;
      int sourceClientHeight = clientHeight;
      if (flag) {
         sourceClientWidth = fixedWidth;
         sourceClientHeight = fixedHeight;
      }

      gameFrame.setPreferredSize(new Dimension(sourceClientWidth + byteCode, sourceClientHeight + byteCode2));
      int localScreenMode = screenMode != 0 && !flag ? minimumWindowWidth : fixedWidth;
      int screenMode2 = screenMode != 0 && !flag ? minimumWindowHeight : fixedHeight;
      gameFrame.setMinimumSize(new Dimension(localScreenMode + byteCode, screenMode2 + byteCode2));
      gameFrame.setResizable(screenMode != 0 && loggedIn);
      gameFrame.setPreferredSize(new Dimension(sourceClientWidth, sourceClientHeight));
      if (ClientWindow.getInstance() != null) {
         gameFrame = ClientWindow.getInstance().frame;
         if (screenMode != 0 && loggedIn) {
            ClientWindow.menuBar.add(ClientWindow.fullscreenMenu);
         } else {
            ClientWindow.menuBar.remove(ClientWindow.fullscreenMenu);
            ClientWindow.menuBar.remove(ClientWindow.windowedModeButton);
         }
      }

      gameFrame.pack();
      super.setSize(sourceClientWidth, sourceClientHeight);
      gameFrame.setLocationRelativeTo(null);

      try {
         saveDisplaySettings();
      } catch (IOException exception) {
         exception.printStackTrace();
      }
   }
   public final void setScreenMode(int newScreenMode) {
      Frame gameFrame = super.gameFrame;
      int clientWidthOrGetWidth = 8;
      byte byteCode = 54;
      if (ClientWindow.getInstance() != null) {
         gameFrame = ClientWindow.getInstance().frame;
         if (!ClientWindow.showTitleBar) {
            clientWidthOrGetWidth = 0;
            byteCode = 21;
         }
      }

      if (newScreenMode == 2) {
         screenMode = 1;
         // Borderless windowed fullscreen uses the desktop bounds of the
         // monitor containing the client. Do not switch the monitor's display
         // mode; keeping the desktop mode is what allows focus to move to a
         // second monitor without the game going black/minimizing.
         Rectangle fullscreenBounds = gameFrame.getGraphicsConfiguration().getBounds();
         clientWidth = fullscreenBounds.width;
         clientHeight = fullscreenBounds.height;
         cameraZoom = 600;
         Client client = this;
         if (super.clientWindow != null) {
            client.clientWindow.frame.dispose();
            client.clientWindow.enterFullscreenMode();
            client.graphics = client.getGameComponent().getGraphics();
         } else {
            client.gameFrame.dispose();
            client.gameFrame.enterFullscreenMode();
            client.graphics = client.getGameComponent().getGraphics();
         }

         if (ClientWindow.getInstance() != null) {
            ClientWindow.menuBar.add(ClientWindow.windowedModeButton);
         }

         // Fullscreen changes clientWidth/clientHeight immediately, so rebuild
         // every raster/image buffer before the next draw pass. Previously the
         // fullscreen branch waited for a later component-resize tick, leaving
         // gameScreenImageProducer at the old window size for one frame. UI
         // scaling then copied regions using the new fullscreen stride and
         // could run past the old pixel buffer (especially at 200% scale).
         this.rebuildViewportBuffers();
      } else {
         if (super.fullscreenActive) {
            super.restoreWindowedMode();
            if (ClientWindow.getInstance() != null) {
               gameFrame = ClientWindow.getInstance().frame;
               ClientWindow.menuBar.remove(ClientWindow.windowedModeButton);
            }
         }

         clientWidthOrGetWidth = gameFrame.getWidth() - clientWidthOrGetWidth;
         int clientHeightOrGetHeight = gameFrame.getHeight() - byteCode;
         if (screenMode != newScreenMode) {
            screenMode = newScreenMode;
            if (newScreenMode != 0) {
               clientWidth = clientWidthOrGetWidth < minimumResizableWidth ? minimumResizableWidth : clientWidthOrGetWidth;
               clientHeight = clientHeightOrGetHeight < minimumResizableHeight ? minimumResizableHeight : clientHeightOrGetHeight;
               cameraZoom = 600;
            } else {
               clientWidth = fixedWidth;
               clientHeight = fixedHeight;
               cameraZoom = 600;
            }

            this.rebuildViewportBuffers();
         }
      }
   }
   private void drawLoadingMessages(int newPlainFont, String text, String newText) {
      newPlainFont = this.plainFont.getTextWidth(newPlainFont == 1 ? text : newText);
      int scalar;
      Rasterizer2D.fillRectangle(scalar = newText == null ? 25 : 38, 1, 1, 0, newPlainFont + 6);
      Rasterizer2D.fillRectangle(1, 1, 1, 16777215, newPlainFont + 6);
      Rasterizer2D.fillRectangle(scalar, 1, 1, 16777215, 1);
      Rasterizer2D.fillRectangle(1, scalar, 1, 16777215, newPlainFont + 6);
      Rasterizer2D.fillRectangle(scalar, 1, newPlainFont + 6, 16777215, 1);
      this.plainFont.textCenter(16777215, text, 18, newPlainFont / 2 + 5);
      if (newText != null) {
         this.plainFont.textCenter(16777215, newText, 31, newPlainFont / 2 + 5);
      }
   }
   private void rebuildViewportBuffers() {
      Rasterizer3D.setViewport(screenMode == 0 ? 765 : clientWidth, screenMode == 0 ? 503 : clientHeight);
      fullScreenScanOffsets = Rasterizer3D.scanOffsets;
      int localClassicChatRasterWidth;
      int localClassicChatRasterHeight;
      if (gameframeVersion != 474) {
         localClassicChatRasterWidth = classicChatRasterWidth;
         localClassicChatRasterHeight = classicChatRasterHeight;
      } else {
         localClassicChatRasterWidth = screenMode == 0 ? modernChatRasterWidth : clientWidth;
         localClassicChatRasterHeight = screenMode == 0 ? modernChatRasterHeight : clientHeight;
      }

      Rasterizer3D.setViewport(localClassicChatRasterWidth, localClassicChatRasterHeight);
      chatboxScanOffsets = Rasterizer3D.scanOffsets;
      if (gameframeVersion != 474) {
         localClassicChatRasterWidth = musicVolume;
         localClassicChatRasterHeight = 261;
      } else {
         localClassicChatRasterWidth = screenMode == 0 ? modernSidebarRasterWidth : clientWidth;
         localClassicChatRasterHeight = screenMode == 0 ? 335 : clientHeight;
      }

      Rasterizer3D.setViewport(localClassicChatRasterWidth, localClassicChatRasterHeight);
      sidebarScanOffsets = Rasterizer3D.scanOffsets;
      Rasterizer3D.setViewport(screenMode == 0 ? 512 : clientWidth, screenMode == 0 ? 334 : clientHeight);
      viewportScanOffsets = Rasterizer3D.scanOffsets;
      if (gameframeVersion == 474) {
         activeChatRasterWidth = modernChatRasterWidth;
         activeChatRasterHeight = modernChatRasterHeight;
         activeSidebarRasterWidth = modernSidebarRasterWidth;
      } else {
         activeChatRasterWidth = classicChatRasterWidth;
         activeChatRasterHeight = classicChatRasterHeight;
         activeSidebarRasterWidth = musicVolume;
      }

      int[] values = new int[9];

      for (int loopIndex = 0; loopIndex < 9; loopIndex++) {
         int sINEIndex = 128 + (loopIndex << 5) + 15;
         int scalar = 600 + sINEIndex * 3;
         sINEIndex = Rasterizer3D.SINE[sINEIndex];
         values[loopIndex] = scalar * sINEIndex >> 16;
      }

      SceneGraph.precalculateTileVisibility(500, 800, screenMode == 0 ? 512 : clientWidth, screenMode == 0 ? 334 : clientHeight, values);
      int localScreenMode = screenMode == 0 ? 512 : clientWidth;
      int screenMode2 = screenMode == 0 ? 334 : clientHeight;
      this.getGameComponent();
      this.gameScreenImageProducer = new BufferedImageGraphicsBuffer(localScreenMode, screenMode2);
      this.frameBuffer = new BufferedImageGraphicsBuffer(clientWidth, clientHeight, (byte)0);
      Rasterizer2D.clear();
      this.setupGameScreenBuffers();
   }
   public static int clampUiScalePercent(int percent) {
      if (percent < MIN_UI_SCALE_PERCENT) {
         return MIN_UI_SCALE_PERCENT;
      }
      if (percent > MAX_UI_SCALE_PERCENT) {
         return MAX_UI_SCALE_PERCENT;
      }
      return percent;
   }

   private static int scaledUiDimension(int baseSize) {
      return Math.max(1, (baseSize * clampUiScalePercent(uiScalePercent) + 50) / 100);
   }

   private static boolean isInsideRectangle(int x, int y, int left, int top, int width, int height) {
      return x >= left && y >= top && x < left + width && y < top + height;
   }

   /**
    * Converts physical mouse coordinates over a scaled resizable/fullscreen UI
    * panel back into the original 2006 UI coordinate space. World/viewport
    * coordinates are returned unchanged.
    */
   public static long translateUiInputCoordinates(int x, int y) {
      if (screenMode == 0 || clampUiScalePercent(uiScalePercent) == 100) {
         return ((long)x << 32) | (y & 0xffffffffL);
      }

      int minimapWidth = scaledUiDimension(RESIZABLE_MINIMAP_UI_WIDTH);
      int minimapHeight = scaledUiDimension(RESIZABLE_MINIMAP_UI_HEIGHT);
      int minimapLeft = clientWidth - minimapWidth;
      if (isInsideRectangle(x, y, minimapLeft, 0, minimapWidth, minimapHeight)) {
         int logicalX = clientWidth - RESIZABLE_MINIMAP_UI_WIDTH
            + (x - minimapLeft) * RESIZABLE_MINIMAP_UI_WIDTH / minimapWidth;
         int logicalY = y * RESIZABLE_MINIMAP_UI_HEIGHT / minimapHeight;
         return ((long)logicalX << 32) | (logicalY & 0xffffffffL);
      }

      int tabWidth = scaledUiDimension(RESIZABLE_TAB_UI_WIDTH);
      int tabHeight = scaledUiDimension(RESIZABLE_TAB_UI_HEIGHT);
      int tabLeft = clientWidth - tabWidth;
      int tabTop = clientHeight - tabHeight;
      if (isInsideRectangle(x, y, tabLeft, tabTop, tabWidth, tabHeight)) {
         int logicalX = clientWidth - RESIZABLE_TAB_UI_WIDTH
            + (x - tabLeft) * RESIZABLE_TAB_UI_WIDTH / tabWidth;
         int logicalY = clientHeight - RESIZABLE_TAB_UI_HEIGHT
            + (y - tabTop) * RESIZABLE_TAB_UI_HEIGHT / tabHeight;
         return ((long)logicalX << 32) | (logicalY & 0xffffffffL);
      }

      int chatWidth = scaledUiDimension(RESIZABLE_CHAT_UI_WIDTH);
      int chatHeight = scaledUiDimension(RESIZABLE_CHAT_UI_HEIGHT);
      int chatTop = clientHeight - chatHeight;
      if (isInsideRectangle(x, y, 0, chatTop, chatWidth, chatHeight)) {
         int logicalX = x * RESIZABLE_CHAT_UI_WIDTH / chatWidth;
         int logicalY = clientHeight - RESIZABLE_CHAT_UI_HEIGHT
            + (y - chatTop) * RESIZABLE_CHAT_UI_HEIGHT / chatHeight;
         return ((long)logicalX << 32) | (logicalY & 0xffffffffL);
      }

      return ((long)x << 32) | (y & 0xffffffffL);
   }

   private void ensureResizableUiScaleBuffers() {
      int chatSize = RESIZABLE_CHAT_UI_WIDTH * RESIZABLE_CHAT_UI_HEIGHT;
      int tabSize = RESIZABLE_TAB_UI_WIDTH * RESIZABLE_TAB_UI_HEIGHT;
      int minimapSize = RESIZABLE_MINIMAP_UI_WIDTH * RESIZABLE_MINIMAP_UI_HEIGHT;
      if (this.uiChatBackground == null || this.uiChatBackground.length != chatSize) {
         this.uiChatBackground = new int[chatSize];
         this.uiChatComposite = new int[chatSize];
      }
      if (this.uiTabBackground == null || this.uiTabBackground.length != tabSize) {
         this.uiTabBackground = new int[tabSize];
         this.uiTabComposite = new int[tabSize];
      }
      if (this.uiMinimapBackground == null || this.uiMinimapBackground.length != minimapSize) {
         this.uiMinimapBackground = new int[minimapSize];
         this.uiMinimapComposite = new int[minimapSize];
      }
   }

   private void copyUiRegion(int[] sourcePixels, int sourceX, int sourceY, int width, int height, int[] destination) {
      int destinationOffset = 0;
      int sourceOffset = sourceY * clientWidth + sourceX;
      for (int row = 0; row < height; ++row) {
         System.arraycopy(sourcePixels, sourceOffset, destination, destinationOffset, width);
         sourceOffset += clientWidth;
         destinationOffset += width;
      }
   }

   private void restoreUiRegion(int[] destinationPixels, int destinationX, int destinationY, int width, int height, int[] source) {
      int sourceOffset = 0;
      int destinationOffset = destinationY * clientWidth + destinationX;
      for (int row = 0; row < height; ++row) {
         System.arraycopy(source, sourceOffset, destinationPixels, destinationOffset, width);
         sourceOffset += width;
         destinationOffset += clientWidth;
      }
   }

   private void drawScaledUiRegion(
      int[] source,
      int[] background,
      int sourceWidth,
      int sourceHeight,
      int destinationX,
      int destinationY,
      int destinationWidth,
      int destinationHeight
   ) {
      int[] destination = this.gameScreenImageProducer.pixels;
      for (int y = 0; y < destinationHeight; ++y) {
         int screenY = destinationY + y;
         if (screenY < 0 || screenY >= clientHeight) {
            continue;
         }
         int sourceY = y * sourceHeight / destinationHeight;
         int sourceRow = sourceY * sourceWidth;
         int destinationRow = screenY * clientWidth;
         for (int x = 0; x < destinationWidth; ++x) {
            int screenX = destinationX + x;
            if (screenX < 0 || screenX >= clientWidth) {
               continue;
            }
            int sourceX = x * sourceWidth / destinationWidth;
            int sourceIndex = sourceRow + sourceX;
            // Only scale pixels actually changed by the UI pass. This keeps
            // the 3D scene underneath transparent/irregular UI regions at its
            // native resolution instead of magnifying the background too.
            if (source[sourceIndex] != background[sourceIndex]) {
               destination[destinationRow + screenX] = source[sourceIndex];
            }
         }
      }
   }

   private boolean shouldScaleResizableUi() {
      return screenMode != 0 && clampUiScalePercent(uiScalePercent) != 100
         && clientWidth >= RESIZABLE_CHAT_UI_WIDTH
         && clientHeight >= RESIZABLE_TAB_UI_HEIGHT
         && this.gameScreenImageProducer != null
         && this.gameScreenImageProducer.getWidth() == clientWidth
         && this.gameScreenImageProducer.getHeight() == clientHeight;
   }

   private boolean shouldUseRawMenuCoordinates() {
      return screenMode != 0 && clampUiScalePercent(uiScalePercent) != 100;
   }

   private int getMenuMouseX() {
      return this.shouldUseRawMenuCoordinates() ? super.rawMouseX : super.mouseX;
   }

   private int getMenuMouseY() {
      return this.shouldUseRawMenuCoordinates() ? super.rawMouseY : super.mouseY;
   }

   private int getMenuClickX() {
      return this.shouldUseRawMenuCoordinates() ? super.rawClickX : super.clickX;
   }

   private int getMenuClickY() {
      return this.shouldUseRawMenuCoordinates() ? super.rawClickY : super.clickY;
   }

   private void captureResizableUiBackground() {
      if (!this.shouldScaleResizableUi()) {
         return;
      }
      this.ensureResizableUiScaleBuffers();
      int[] pixels = this.gameScreenImageProducer.pixels;
      this.copyUiRegion(
         pixels,
         0,
         clientHeight - RESIZABLE_CHAT_UI_HEIGHT,
         RESIZABLE_CHAT_UI_WIDTH,
         RESIZABLE_CHAT_UI_HEIGHT,
         this.uiChatBackground
      );
      this.copyUiRegion(
         pixels,
         clientWidth - RESIZABLE_TAB_UI_WIDTH,
         clientHeight - RESIZABLE_TAB_UI_HEIGHT,
         RESIZABLE_TAB_UI_WIDTH,
         RESIZABLE_TAB_UI_HEIGHT,
         this.uiTabBackground
      );
      this.copyUiRegion(
         pixels,
         clientWidth - RESIZABLE_MINIMAP_UI_WIDTH,
         0,
         RESIZABLE_MINIMAP_UI_WIDTH,
         RESIZABLE_MINIMAP_UI_HEIGHT,
         this.uiMinimapBackground
      );
   }

   private void scaleResizableUiAfterRender() {
      if (!this.shouldScaleResizableUi()) {
         return;
      }

      int[] pixels = this.gameScreenImageProducer.pixels;
      int chatY = clientHeight - RESIZABLE_CHAT_UI_HEIGHT;
      int tabX = clientWidth - RESIZABLE_TAB_UI_WIDTH;
      int tabY = clientHeight - RESIZABLE_TAB_UI_HEIGHT;
      int minimapX = clientWidth - RESIZABLE_MINIMAP_UI_WIDTH;

      this.copyUiRegion(
         pixels, 0, chatY, RESIZABLE_CHAT_UI_WIDTH, RESIZABLE_CHAT_UI_HEIGHT, this.uiChatComposite
      );
      this.copyUiRegion(
         pixels, tabX, tabY, RESIZABLE_TAB_UI_WIDTH, RESIZABLE_TAB_UI_HEIGHT, this.uiTabComposite
      );
      this.copyUiRegion(
         pixels, minimapX, 0, RESIZABLE_MINIMAP_UI_WIDTH, RESIZABLE_MINIMAP_UI_HEIGHT, this.uiMinimapComposite
      );

      // Remove the normal-size panels before placing their scaled versions.
      this.restoreUiRegion(
         pixels, 0, chatY, RESIZABLE_CHAT_UI_WIDTH, RESIZABLE_CHAT_UI_HEIGHT, this.uiChatBackground
      );
      this.restoreUiRegion(
         pixels, tabX, tabY, RESIZABLE_TAB_UI_WIDTH, RESIZABLE_TAB_UI_HEIGHT, this.uiTabBackground
      );
      this.restoreUiRegion(
         pixels, minimapX, 0, RESIZABLE_MINIMAP_UI_WIDTH, RESIZABLE_MINIMAP_UI_HEIGHT, this.uiMinimapBackground
      );

      int chatWidth = scaledUiDimension(RESIZABLE_CHAT_UI_WIDTH);
      int chatHeight = scaledUiDimension(RESIZABLE_CHAT_UI_HEIGHT);
      int tabWidth = scaledUiDimension(RESIZABLE_TAB_UI_WIDTH);
      int tabHeight = scaledUiDimension(RESIZABLE_TAB_UI_HEIGHT);
      int minimapWidth = scaledUiDimension(RESIZABLE_MINIMAP_UI_WIDTH);
      int minimapHeight = scaledUiDimension(RESIZABLE_MINIMAP_UI_HEIGHT);

      // Preserve the same anchor points as the unscaled resizable UI.
      this.drawScaledUiRegion(
         this.uiChatComposite,
         this.uiChatBackground,
         RESIZABLE_CHAT_UI_WIDTH,
         RESIZABLE_CHAT_UI_HEIGHT,
         0,
         clientHeight - chatHeight,
         chatWidth,
         chatHeight
      );
      this.drawScaledUiRegion(
         this.uiTabComposite,
         this.uiTabBackground,
         RESIZABLE_TAB_UI_WIDTH,
         RESIZABLE_TAB_UI_HEIGHT,
         clientWidth - tabWidth,
         clientHeight - tabHeight,
         tabWidth,
         tabHeight
      );
      this.drawScaledUiRegion(
         this.uiMinimapComposite,
         this.uiMinimapBackground,
         RESIZABLE_MINIMAP_UI_WIDTH,
         RESIZABLE_MINIMAP_UI_HEIGHT,
         clientWidth - minimapWidth,
         0,
         minimapWidth,
         minimapHeight
      );

      this.gameScreenImageProducer.initDrawingArea();
   }

   @Override
   public final void handleMouseWheel(MouseWheelEvent mouseWheelEvent) {
      int wheelRotation = mouseWheelEvent.getWheelRotation();
      int localChildX = 0;
      int localChildY = 0;
      int localWidgets = 0;
      int widgets2 = 0;
      int childIdIndex2 = 0;
      int widgetIndex;
      if ((widgetIndex = this.tabInterfaceIds[this.currentTab]) != -1) {
         Widget widget = Widget.widgets[widgetIndex];
         widgetIndex = screenMode == 0 ? 547 : clientWidth - 197;
         int localScreenMode = screenMode == 0 ? 205 : clientHeight - (clientWidth >= this.wideTabBarWidthThreshold ? 37 : 74) - 267;

         for (int childIdIndex = 0; childIdIndex < widget.childIds.length; childIdIndex++) {
            if (Widget.widgets[widget.childIds[childIdIndex]].scrollMax > 0) {
               childIdIndex2 = childIdIndex;
               localChildX = widget.childX[childIdIndex];
               localChildY = widget.childY[childIdIndex];
               localWidgets = Widget.widgets[widget.childIds[childIdIndex]].width;
               widgets2 = Widget.widgets[widget.childIds[childIdIndex]].height;
               break;
            }
         }

         if (this.mouseX > widgetIndex + localChildX && this.mouseY > localScreenMode + localChildY && this.mouseX < widgetIndex + localChildX + localWidgets && this.mouseY < localScreenMode + localChildY + widgets2) {
            Widget.widgets[widget.childIds[childIdIndex2]].scrollPosition += wheelRotation * 30;
            this.tabAreaAltered = true;
            this.needDrawTabArea = true;
         }
      }

      if (this.openInterfaceId != -1) {
         Widget widget2 = Widget.widgets[this.openInterfaceId];
         int widgets3 = 1;
         if (shouldCenterInterface(widget2)) {
            widgets3 = 0;
         }

         widgetIndex = widgets3 != 0 ? 4 : clientWidth / 2 - 256;
         int scalar = widgets3 != 0 ? 4 : clientHeight / 2 - 167;

         for (int childIdIndex3 = 0; childIdIndex3 < widget2.childIds.length; childIdIndex3++) {
            if (Widget.widgets[widget2.childIds[childIdIndex3]].scrollMax > 0) {
               childIdIndex2 = childIdIndex3;
               localChildX = widget2.childX[childIdIndex3];
               localChildY = widget2.childY[childIdIndex3];
               localWidgets = Widget.widgets[widget2.childIds[childIdIndex3]].width;
               widgets2 = Widget.widgets[widget2.childIds[childIdIndex3]].height;
               break;
            }
         }

         if (this.mouseX > widgetIndex + localChildX && this.mouseY > scalar + localChildY && this.mouseX < widgetIndex + localChildX + localWidgets && this.mouseY < scalar + localChildY + widgets2) {
            widgets3 = Widget.widgets[widget2.childIds[childIdIndex2]].scrollPosition;
            int scalar2 = wheelRotation * 30;
            if ((localChildX = widgets3 + scalar2) < 0) {
               scalar2 = -widgets3;
            } else if (localChildX > Widget.widgets[widget2.childIds[childIdIndex2]].scrollMax - Widget.widgets[widget2.childIds[childIdIndex2]].height) {
               scalar2 = Widget.widgets[widget2.childIds[childIdIndex2]].scrollMax - Widget.widgets[widget2.childIds[childIdIndex2]].height - widgets3;
            }

            Widget.widgets[widget2.childIds[childIdIndex2]].scrollPosition += scalar2;
            this.dragStartY -= scalar2;
         }
      }
   }
   private void replyToLastPrivateMessage() {
      String text = null;

      for (int chatMessageIndex = 0; chatMessageIndex < 100; chatMessageIndex++) {
         int localChatTypes;
         if (this.chatMessages[chatMessageIndex] != null && ((localChatTypes = this.chatTypes[chatMessageIndex]) == 3 || localChatTypes == 7)) {
            text = this.chatNames[chatMessageIndex];
            break;
         }
      }

      if (text == null) {
         this.pushMessage("You haven't received any messages to which you can reply.", 0, "", 0, 0, 0);
      } else {
         long encodedName = NameUtils.encodeBase37(text.trim());
         int friendWorldIndex = -1;

         for (int friendEncodedNameIndex = 0; friendEncodedNameIndex < this.friendCount; friendEncodedNameIndex++) {
            if (this.friendEncodedNames[friendEncodedNameIndex] == encodedName) {
               friendWorldIndex = friendEncodedNameIndex;
               break;
            }
         }

         if (friendWorldIndex != -1) {
            if (this.friendWorlds[friendWorldIndex] > 0) {
               this.inputTaken = true;
               this.inputDialogState = 0;
               this.messagePromptRaised = true;
               this.promptInput = "";
               this.friendsListAction = 3;
               this.privateMessageTarget = this.friendEncodedNames[friendWorldIndex];
               this.promptMessage = "Enter message to send to " + this.friendNames[friendWorldIndex];
               return;
            }

            this.pushMessage("That player is currently offline.", 0, "", 0, 0, 0);
         }
      }
   }
   @Override
   final void handleMiddleMouseDrag(int scalarArgument, int scalarArgument2) {
      if (this.middleMouseDown) {
         this.cameraYawVelocity += scalarArgument * 3;
         this.cameraPitchVelocity += scalarArgument2 << 1;
      }
   }
   @Override
   public final void sendClickTimingReport(int scalarArgument) {
      if (loggedIn) {
         this.outgoingBuffer.writeOpcode(11);
         this.outgoingBuffer.writeByte(1);
      }
   }
   private static void addExperienceDrop(int currentExpIndex, int scalarArgument) {
      if (scalarArgument > 0 && currentExpIndex >= 0) {
         new ExperienceDrop(currentExpIndex, scalarArgument);
      }
   }
   private void drawExperienceDrops() {
      int scalar = -1;
      int localProjectedEntityX;
      int localProjectedEntityY;
      if (xpDropPosition == 2) {
         localProjectedEntityX = this.projectedEntityX;
         localProjectedEntityY = this.projectedEntityY;
         if (screenMode != 0) {
            scalar = localProjectedEntityY - 100;
         }
      } else {
         localProjectedEntityX = screenMode == 0 ? 510 : clientWidth - 250;
         localProjectedEntityY = 100;
      }

      int scalar2 = 0;
      BitmapFont bitmapFont = null;
      if (xpDropSize == 0) {
         bitmapFont = this.smallFont;
      } else if (xpDropSize == 1) {
         bitmapFont = this.plainFont;
      } else if (xpDropSize == 2) {
         bitmapFont = this.boldFont;
      }

      Iterator iterator = ExperienceDrop.drops.iterator();

      while (iterator.hasNext()) {
         ExperienceDrop experienceDrop;
         if ((experienceDrop = (ExperienceDrop)iterator.next()) != null) {
            if (experienceDrop.y == -1) {
               experienceDrop.y = localProjectedEntityY + scalar2 * 24;
            }

            String text = "+" + this.numberFormat.format(experienceDrop.experience) + " xp";
            int measuredTextWidth = bitmapFont.getTextWidth(text);
            int scalar3 = localProjectedEntityX - measuredTextWidth;
            int sourceMeasuredTextWidth = measuredTextWidth;
            int localSpriteWidth = 0;
            if (xpDropSize > 0) {
               scalar3 -= 5;
            }

            Sprite sprite;
            if (xpDropSize > 0) {
               sprite = this.skillIconSprites[experienceDrop.skillId];
            } else {
               sprite = customSprites[53 + experienceDrop.skillId];
            }

            if (sprite != null) {
               localSpriteWidth = sprite.spriteWidth + 3;
               if (xpDropPosition == 2 && xpDropSize > 0) {
                  localSpriteWidth = sprite.spriteWidth + 8;
               }

               sourceMeasuredTextWidth += localSpriteWidth;
               if (xpDropPosition == 1) {
                  sprite.drawOutlinedSprite(scalar3 - localSpriteWidth, experienceDrop.y - sprite.spriteHeight, 0);
               } else {
                  sprite.drawOutlinedSprite(localProjectedEntityX - sourceMeasuredTextWidth / 2, experienceDrop.y - sprite.spriteHeight, 0);
               }
            }

            if (xpDropPosition == 1) {
               bitmapFont.textLeftShadow(true, localProjectedEntityX - measuredTextWidth, xpDropColor, text, experienceDrop.y);
            } else {
               bitmapFont.textLeftShadow(true, localProjectedEntityX - sourceMeasuredTextWidth / 2 + localSpriteWidth, xpDropColor, text, experienceDrop.y);
            }

            scalar2++;
            experienceDrop.y--;
            if (experienceDrop.y == scalar) {
               iterator.remove();
            }
         }
      }
   }
   private static String wrapText(String text, int scalarArgument, BitmapFont bitmapFont) {
      if (bitmapFont.getTextWidth(text) > scalarArgument) {
         String[] parts = text.split(" ");
         String localText = "";
         int measuredTextWidth = 0;

         for (int partIndex = 0; partIndex < parts.length; partIndex++) {
            String text2 = parts[partIndex] + " ";
            if ((measuredTextWidth += bitmapFont.getTextWidth(text2)) <= scalarArgument) {
               localText = localText + text2;
            } else {
               localText = localText + "\\n";
               text2 = parts[partIndex] + " ";
               localText = localText + text2;
               measuredTextWidth = bitmapFont.getTextWidth(text2);
            }
         }

         return localText;
      } else {
         return text;
      }
   }
   private void updateSeasonalTheme() {
      this.customSettingShowExperiencePerHourStartLevels = this.cButtonCPos;
      christmasEventActive = false;
      halloweenEventActive = false;
      this.setChannel = 0;
      Client client = this;
      Calendar calendar = Calendar.getInstance();
      Calendar calendar2;
      (calendar2 = Calendar.getInstance()).set(2, 11);
      calendar2.set(5, 24);
      calendar.setTimeZone(client.timeZone);
      calendar2.setTimeZone(client.timeZone);
      int retrievedEntry = calendar2.get(6);
      if (Math.abs(calendar.get(6) - retrievedEntry) <= 7) {
         this.customSettingShowExperiencePerHourStartLevels = this.cButtonHPos;
         christmasEventActive = true;
         this.setChannel = 1;
      }

      client = this;
      calendar = Calendar.getInstance();
      (calendar2 = Calendar.getInstance()).set(2, 9);
      calendar2.set(5, 31);
      calendar.setTimeZone(client.timeZone);
      calendar2.setTimeZone(client.timeZone);
      int get2 = calendar2.get(6);
      if (Math.abs(calendar.get(6) - get2) <= 7) {
         this.customSettingShowExperiencePerHourStartLevels = this.WORLD;
         halloweenEventActive = true;
         this.setChannel = 2;
      }
   }
   private void drawItemSearch() {
      int localScreenMode = screenMode != 0 ? 7 : 0;
      int screenMode2 = screenMode != 0 ? clientHeight - 158 : 0;

      try {
         if (this.amountOrNameInput != "") {
            String text = this.amountOrNameInput;
            Client client = this;
            if (text != null && text.length() != 0) {
               String[] localText = new String[100];
               int loopIndex = 0;

               int position;
               while ((position = text.indexOf(" ")) != -1) {
                  String text2;
                  if ((text2 = text.substring(0, position).trim()).length() > 0) {
                     localText[loopIndex++] = text2.toLowerCase();
                  }

                  text = text.substring(position + 1);
               }

               if ((text = text.trim()).length() > 0) {
                  localText[loopIndex++] = text.toLowerCase();
               }

               client.clanChatMode = 0;

               itemSearchLoop:
               for (int loopIndex2 = 0; loopIndex2 < ItemDefinition.getDefinitionCount(); loopIndex2++) {
                  ItemDefinition itemDefinition;
                  if ((loopIndex2 < 7956 || loopIndex2 > 8118) && (itemDefinition = ItemDefinition.lookup(loopIndex2)) != null && (itemDefinition.searchable || loopIndex2 == 14484)) {
                     text = itemDefinition.name.toLowerCase();

                     for (int localTextIndex = 0; localTextIndex < loopIndex; localTextIndex++) {
                        if (text.indexOf(localText[localTextIndex]) == -1) {
                           continue itemSearchLoop;
                        }
                     }

                     client.itemSearchResultNames[client.clanChatMode] = text;
                     client.itemSearchResultIds[client.clanChatMode] = itemDefinition.id;
                     client.clanChatMode++;
                     if (client.clanChatMode >= client.itemSearchResultNames.length) {
                        break;
                     }
                  }
               }
            } else {
               client.clanChatMode = 0;
            }
         }

         BitmapFont bitmapFont = this.plainFont;
         byte byteCode = 8;
         byte byteCode2 = 126;
         byte byteCode3 = 30;
         byte height = 114;
         short shortCode = 489;
         short shortCode2 = 495;
         int screenMode3 = screenMode == 0 ? 345 : clientHeight - 155;
         if (gameframeVersion != 474) {
            byteCode = 5;
            byteCode2 = 90;
            byteCode3 = 10;
            height = 77;
            shortCode = 463;
            shortCode2 = 478;
            screenMode3 = 357;
         }

         customSprites[51].drawSprite(localScreenMode + 18, screenMode2 + 18);

         for (int itemSearchResultNameIndex = 0; itemSearchResultNameIndex < this.clanChatMode; itemSearchResultNameIndex++) {
            int mouseX = this.getMenuMouseX();
            int mouseY = this.getMenuMouseY();
            int scalar;
            if ((scalar = itemSearchResultNameIndex * 14 - this.autoCastId + 14) > 0 && scalar < height + 1) {
               bitmapFont.textLeft(10508800, formatItemSearchName(this.itemSearchResultNames[itemSearchResultNameIndex]), scalar + screenMode2, localScreenMode + 77);
               if (mouseX >= 83 && mouseX <= shortCode2 && mouseY > screenMode3 + scalar - 13 && mouseY < screenMode3 + scalar + 2 && !this.menuOpen) {
                  Rasterizer2D.fillRectangleAlpha(8418912, scalar - 12 + screenMode2, 431, 15, 60, localScreenMode + 75);
                  Sprite sprite = ItemDefinition.getSprite(this.itemSearchResultIds[itemSearchResultNameIndex], 1, 0);
                  this.chatTypeView = this.itemSearchResultIds[itemSearchResultNameIndex];
                  if (sprite != null) {
                     sprite.drawSprite(localScreenMode + 22, screenMode2 + 20);
                  }

                  if (this.yCameraCurve >= 2) {
                     bitmapFont.textLeft(0, "Id: " + this.itemSearchResultIds[itemSearchResultNameIndex], byteCode2 - 16 + screenMode2, localScreenMode + 10);
                  }
               }
            }

            if (this.menuOpen && this.chatTypeView != -1) {
               Sprite sprite2;
               if ((sprite2 = ItemDefinition.getSprite(this.chatTypeView, 1, 0)) != null) {
                  sprite2.drawSprite(localScreenMode + 22, screenMode2 + 20);
               }

               if (this.yCameraCurve >= 2) {
                  bitmapFont.textLeft(0, "Id: " + this.chatTypeView, byteCode2 - 16 + screenMode2, localScreenMode + 10);
               }
            }
         }

         Rasterizer2D.fillRectangle(byteCode2 - 12, screenMode2 + 0, localScreenMode + 74, 8418912, 2);
         Rasterizer2D.resetClip();
         if (this.clanChatMode > byteCode) {
            this.drawScrollBar(height, this.autoCastId, screenMode2 + 0, shortCode + localScreenMode, this.clanChatMode * 14, false);
         }

         if (this.amountOrNameInput.length() == 0) {
            String text3 = "Grand Exchange Item Search";
            if (itemSearchSpawnMode) {
               text3 = "Item Search";
            }

            this.boldFont.textCenter(10508800, text3, byteCode3 + screenMode2, localScreenMode + 280);
            this.smallFont.textCenterShadow(10508800, localScreenMode + 280, "To search for an item, start by typing part of it's name.", byteCode3 + 35 + screenMode2, false);
            this.smallFont.textCenterShadow(10508800, localScreenMode + 280, "Then, simply select the item you want from the results on display.", byteCode3 + 50 + screenMode2, false);
         } else if (this.clanChatMode == 0) {
            this.smallFont.textCenterShadow(10508800, localScreenMode + 280, "No matching items found!", byteCode3 + 35 + screenMode2, false);
         }

         Rasterizer2D.fillRectangleAlpha(8418912, byteCode2 - 12 + screenMode2, 506 - (screenMode == 0 ? 0 : 1), 15, 120, localScreenMode + 0);
         bitmapFont.textLeftShadow(true, localScreenMode + 25, 16777215, this.amountOrNameInput + "*", byteCode2 + screenMode2);
         customSprites[52].drawSprite(localScreenMode + 4, byteCode2 - 11 + screenMode2);
         Rasterizer2D.drawHorizontalLine(byteCode2 - 12 + screenMode2, 8418912, 506 - (screenMode == 0 ? 0 : 1), localScreenMode + 0);
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }
   private void buildItemSearchMenu(int mouseY) {
      if (screenMode != 0) {
         ;
      }

      int scalar = 0;
      byte byteCode = 114;
      short shortCode = 495;
      int localScreenMode = screenMode == 0 ? 345 : clientHeight - 155;
      if (gameframeVersion != 474) {
         byteCode = 77;
         shortCode = 478;
         localScreenMode = 357;
      }

      for (int itemSearchResultNameIndex = 0; itemSearchResultNameIndex < 100; itemSearchResultNameIndex++) {
         if (this.amountOrNameInput.length() == 0 || this.amountOrNameInput == "" || this.clanChatMode == 0) {
            return;
         }

         formatItemSearchName(this.itemSearchResultNames[itemSearchResultNameIndex]);
         int scalar2;
         if ((scalar2 = scalar * 14 - this.autoCastId) > 0
            && scalar2 < byteCode + 1
            && mouseY > localScreenMode + scalar2 - 13
            && mouseY < localScreenMode + scalar2 + 2
            && super.mouseX >= 83
            && super.mouseX <= shortCode) {
            if (!itemSearchSpawnMode) {
               this.menuActionNames[this.menuActionCount] = "Select";
               this.menuActionIds[this.menuActionCount] = 1250;
            } else {
               this.menuActionNames[this.menuActionCount] = "Spawn";
               this.menuActionIds[this.menuActionCount] = 1251;
            }

            this.menuActionCount++;
         }

         scalar++;
      }
   }
   private static String formatItemSearchName(String text) {
      try {
         if (text != "") {
            return (text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase()).trim();
         }
      } catch (Exception exception) {
      }

      return text;
   }
   private static void saveDisplaySettings() throws IOException {
      DataOutputStream dataOutputStream;
      (dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(SignLink.findcachedir() + "settings.dat")))).writeInt(gameframeVersion);
      dataOutputStream.writeInt(musicVolumeSetting);
      dataOutputStream.writeBoolean(transparentTabArea);
      dataOutputStream.writeBoolean(orbsEnabled);
      dataOutputStream.writeByte(screenMode);
      dataOutputStream.writeShort(clientWidth);
      dataOutputStream.writeShort(clientHeight);
      dataOutputStream.writeShort(cameraZoom);
      dataOutputStream.writeBoolean(osrsResizableFrame);
      dataOutputStream.close();
   }
   private static void loadDisplaySettings() throws IOException {
      DataInputStream dataInputStream;
      gameframeVersion = (dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(SignLink.findcachedir() + "settings.dat")))).readInt();
      musicVolumeSetting = dataInputStream.readInt();
      transparentTabArea = dataInputStream.readBoolean();
      orbsEnabled = dataInputStream.readBoolean();
      screenMode = dataInputStream.readByte();
      clientWidth = dataInputStream.readShort();
      clientHeight = dataInputStream.readShort();
      cameraZoom = dataInputStream.readShort();
      osrsResizableFrame = dataInputStream.readBoolean();
      dataInputStream.close();
   }
   public static void loadUserConfig() throws IOException {
      String text = "./userConfig.cfg";
      if (!new File(text).exists()) {
         ClientSettings.createDefaultConfig();
      }

      String text2 = "";
      BufferedReader bufferedReader = null;

      try {
         bufferedReader = new BufferedReader(new FileReader("./" + text));
      } catch (FileNotFoundException exception) {
         System.out.println(text + ": file not found.");
      }

      try {
         text2 = bufferedReader.readLine();
      } catch (IOException exception2) {
         System.out.println(text + ": error loading file.");
      }

      for (; text2 != null; text2 = bufferedReader.readLine()) {
         if (text2.startsWith("[")) {
            String[] parts;
            text2 = (parts = text2.split(";"))[0].replace("[", "").replace("]", "");
            String[] localLength = new String[parts.length - 1];

            for (int localLengthIndex = 0; localLengthIndex < parts.length - 1; localLengthIndex++) {
               localLength[localLengthIndex] = parts[localLengthIndex + 1];
            }

            ClientSettings.applySetting(text2, localLength);
         }
      }

      bufferedReader.close();
   }
   private static String formatAmountLong(int inventoryAmount) {
      String text = String.valueOf(inventoryAmount);
      for (int loopIndex = text.length() - 3; loopIndex > 0; loopIndex -= 3) {
         text = text.substring(0, loopIndex) + "," + text.substring(loopIndex);
      }

      if (text.length() > 8) {
         text = "@gre@" + text.substring(0, text.length() - 8) + " million @whi@(" + text + ")";
      } else if (text.length() > 4) {
         text = "@cya@" + text.substring(0, text.length() - 4) + "K @whi@(" + text + ")";
      }

      return " " + text;
   }
   private static void signalMidiStop() {
      SignLink.unusedPublicInt = 0;
      SignLink.unusedPublicString1 = "stop";
   }
   private boolean isAddFriendMenuAction(int newMenuActionIds) {
      if (newMenuActionIds < 0) {
         return false;
      }

      if ((newMenuActionIds = this.menuActionIds[newMenuActionIds]) >= 2000) {
         newMenuActionIds -= 2000;
      }

      return newMenuActionIds == 337;
   }
   private void drawChannelButtons() {
      int localScreenMode = screenMode != 0 ? 5 : 0;
      int screenMode2 = screenMode != 0 ? clientHeight - 23 : 0;
      if (screenMode == 0) {
         customSprites[14].drawSprite(0, 0);
      } else {
         customSprites[94].drawSprite(localScreenMode + 0, screenMode2 + 0);
      }

      String[] text = new String[]{"On", "Friends", "Off", "Hide"};
      int[] values = new int[]{65280, 16776960, 16711680, 65535};
      localScreenMode -= screenMode != 0 ? 5 : 0;
      screenMode2 -= screenMode != 0 ? 4 : 0;
      switch (tiara) {
         case 0:
            customSprites[34].drawSprite(localScreenMode + 5, screenMode2 + 4);
            break;
         case 1:
            customSprites[34].drawSprite(localScreenMode + 71, screenMode2 + 4);
            break;
         case 2:
            customSprites[34].drawSprite(localScreenMode + 137, screenMode2 + 4);
            break;
         case 3:
            customSprites[34].drawSprite(localScreenMode + 203, screenMode2 + 4);
            break;
         case 4:
            customSprites[34].drawSprite(localScreenMode + 269, screenMode2 + 4);
            break;
         case 5:
            customSprites[34].drawSprite(localScreenMode + 335, screenMode2 + 4);
      }

      if (this.hoveredChatMode == tiara) {
         switch (this.hoveredChatMode) {
            case 0:
               customSprites[35].drawSprite(localScreenMode + 5, screenMode2 + 4);
               break;
            case 1:
               customSprites[35].drawSprite(localScreenMode + 71, screenMode2 + 4);
               break;
            case 2:
               customSprites[35].drawSprite(localScreenMode + 137, screenMode2 + 4);
               break;
            case 3:
               customSprites[35].drawSprite(localScreenMode + 203, screenMode2 + 4);
               break;
            case 4:
               customSprites[35].drawSprite(localScreenMode + 269, screenMode2 + 4);
               break;
            case 5:
               customSprites[35].drawSprite(localScreenMode + 335, screenMode2 + 4);
               break;
            case 6:
               customSprites[36].drawSprite(localScreenMode + 404, screenMode2 + 4);
         }
      } else {
         switch (this.hoveredChatMode) {
            case 0:
               customSprites[33].drawSprite(localScreenMode + 5, screenMode2 + 4);
               break;
            case 1:
               customSprites[33].drawSprite(localScreenMode + 71, screenMode2 + 4);
               break;
            case 2:
               customSprites[33].drawSprite(localScreenMode + 137, screenMode2 + 4);
               break;
            case 3:
               customSprites[33].drawSprite(localScreenMode + 203, screenMode2 + 4);
               break;
            case 4:
               customSprites[33].drawSprite(localScreenMode + 269, screenMode2 + 4);
               break;
            case 5:
               customSprites[33].drawSprite(localScreenMode + 335, screenMode2 + 4);
               break;
            case 6:
               customSprites[36].drawSprite(localScreenMode + 404, screenMode2 + 4);
         }
      }

      this.smallFont.textLeftShadow(true, localScreenMode + 425, 16777215, "Report Abuse", screenMode2 + 19);
      this.smallFont.textLeftShadow(true, localScreenMode + 26, 16777215, "All", screenMode2 + 19);
      this.smallFont.textLeftShadow(true, localScreenMode + 86, 16777215, "Game", screenMode2 + 19);
      this.smallFont.textLeftShadow(true, localScreenMode + 150, 16777215, "Public", screenMode2 + 14);
      this.smallFont.textLeftShadow(true, localScreenMode + 212, 16777215, "Private", screenMode2 + 14);
      this.smallFont.textLeftShadow(true, localScreenMode + 286, 16777215, "Clan", screenMode2 + 14);
      this.smallFont.textLeftShadow(true, localScreenMode + 349, 16777215, "Trade", screenMode2 + 14);
      // Keep the second-line channel status text inside the source HUD
      // rectangle. In resizable/fullscreen mode these buttons sit flush with
      // the bottom edge; rendering at +25 clips the bottom row of the glyphs
      // before the UI-scaling pass captures them, and scaling magnifies that
      // clipping. Lift only the resizable status baselines slightly.
      int channelStatusTextY = screenMode2 + (this.shouldScaleResizableUi() ? 20 : 25);
      this.smallFont.textCenterShadow(values[this.publicChatMode], localScreenMode + 164, text[this.publicChatMode], channelStatusTextY, true);
      this.smallFont.textCenterShadow(values[this.privateChatMode], localScreenMode + 230, text[this.privateChatMode], channelStatusTextY, true);
      this.smallFont.textCenterShadow(values[this.tradeMode], localScreenMode + 362, text[this.tradeMode], channelStatusTextY, true);
   }
   private void drawChatArea() {
      int localScreenMode = screenMode != 0 ? 7 : 0;
      int screenMode2 = screenMode != 0 ? clientHeight - 158 : 0;
      if (this.messagePromptRaised || this.inputDialogState != 0 || this.clickToContinueString != null || this.backDialogID != -1 || this.dialogID != -1) {
         this.chatMessagesVisible = true;
      }

      if (this.chatMessagesVisible && screenMode != 0) {
         if (this.messagePromptRaised || this.inputDialogState != 0 || this.clickToContinueString != null || this.backDialogID != -1 || this.dialogID != -1 || !hdChatbox) {
            customSprites[92].drawSprite(localScreenMode + 0 - 7, screenMode2 + 0 - 7);
            customSprites[93].drawSpriteAlpha(localScreenMode + 7 - 7, screenMode2 + 7 - 7, 192);
         } else if (hdChatbox) {
            screenMode2 += 5;
            int sourcePixelIndex = localScreenMode + 0;
            int scalar = screenMode2 + 0;
            byte byteCode = 70;
            int pixelIndex = 0;
            pixelIndex = 0;
            int position = 130;
            int position2 = 505;
            int topY = scalar;
            pixelIndex = sourcePixelIndex;
            int scalar2 = 0;
            if (pixelIndex < Rasterizer2D.topX) {
               position2 = 505 - (Rasterizer2D.topX - pixelIndex);
               pixelIndex = Rasterizer2D.topX;
            }

            if (topY < Rasterizer2D.topY) {
               scalar2 = 0 - (Rasterizer2D.topY - topY) * 504;
               position = 130 - (Rasterizer2D.topY - topY);
               topY = Rasterizer2D.topY;
            }

            if (pixelIndex + position2 > Rasterizer2D.bottomX) {
               position2 = Rasterizer2D.bottomX - pixelIndex;
            }

            if (topY + position > Rasterizer2D.bottomY) {
               position = Rasterizer2D.bottomY - topY;
            }

            int localWidth = Rasterizer2D.width - position2;
            pixelIndex += topY * Rasterizer2D.width;

            for (int loopIndex = -position; loopIndex < 0; loopIndex++) {
               int scalar3 = (position + loopIndex) * (position / 70);
               scalar3 = 256 - scalar3;
               int pixel = 65536 - scalar2 >> 8;
               int scalar4 = scalar2 >> 8;
               int scalar5;
               pixel = (((scalar5 = (0 * pixel + 0 * scalar4 & -16711936) + (0 * pixel + 0 * scalar4 & 0xFF0000) >>> 8) & 16711935) * 70 >> 8 & 16711935)
                  + ((scalar5 & 0xFF00) * 70 >> 8 & 0xFF00);

               for (int loopIndex2 = -position2; loopIndex2 < 0; loopIndex2++) {
                  int localPixels;
                  localPixels = (((localPixels = Rasterizer2D.pixels[pixelIndex]) & 16711935) * scalar3 >> 8 & 16711935) + ((localPixels & 0xFF00) * scalar3 >> 8 & 0xFF00);
                  Rasterizer2D.pixels[pixelIndex++] = pixel + localPixels;
               }

               pixelIndex += localWidth;
               scalar2 -= 504;
            }
         }
      }

      if (screenMode != 0) {
         this.drawChannelButtons();
      }

      if (screenMode == 0) {
         this.chatboxImageProducer.initDrawingArea();
         Rasterizer3D.scanOffsets = chatboxScanOffsets;
         if (gameframeVersion != 474) {
            this.chatBack.drawBackground(0, 0);
         } else {
            customSprites[15].drawSprite(0, 0);
         }
      }

      int localChatContentHeight = 0;
      if (gameframeVersion == 474) {
         localChatContentHeight = 15;
      }

      BitmapFont bitmapFont = this.plainFont;
      if (this.messagePromptRaised) {
         this.boldFont.textCenter(0, this.promptMessage, localChatContentHeight + 40 + screenMode2, localChatContentHeight + 239 + localScreenMode);
         this.boldFont.textCenter(128, this.promptInput + "*", localChatContentHeight + 60 + screenMode2, localChatContentHeight + 239 + localScreenMode);
      } else if (this.inputDialogState == 1) {
         this.boldFont.textCenter(0, "Enter amount:", localChatContentHeight + 40 + screenMode2, localChatContentHeight + 239 + localScreenMode);
         this.boldFont.textCenter(128, this.amountOrNameInput + "*", localChatContentHeight + 60 + screenMode2, localChatContentHeight + 239 + localScreenMode);
      } else if (this.inputDialogState == 2) {
         if (this.openInterfaceId == 5292) {
            this.boldFont.textCenter(0, "Enter the name of the item you wish to search for:", localChatContentHeight + 40 + screenMode2, localChatContentHeight + 239 + localScreenMode);
         } else {
            this.boldFont.textCenter(0, "Enter name:", localChatContentHeight + 40 + screenMode2, localChatContentHeight + 239 + localScreenMode);
         }

         this.boldFont.textCenter(128, this.amountOrNameInput + "*", localChatContentHeight + 60 + screenMode2, localChatContentHeight + 239 + localScreenMode);
      } else if (this.inputDialogState == 3) {
         this.drawItemSearch();
      } else if (this.clickToContinueString != null) {
         this.boldFont.textCenter(0, this.clickToContinueString, localChatContentHeight + 40 + screenMode2, localChatContentHeight + 239 + localScreenMode);
         this.boldFont.textCenter(128, "Click to continue", localChatContentHeight + 60 + screenMode2, localChatContentHeight + 239 + localScreenMode);
      } else if (this.backDialogID != -1) {
         this.drawInterface(0, localChatContentHeight + 0 + localScreenMode, Widget.widgets[this.backDialogID], localChatContentHeight + 0 + screenMode2);
      } else if (this.dialogID != -1) {
         this.drawInterface(0, localChatContentHeight + 0 + localScreenMode, Widget.widgets[this.dialogID], localChatContentHeight + 0 + screenMode2);
      } else if (this.chatMessagesVisible) {
         localChatContentHeight = 0;
         int scalar6 = -3;
         short shortCode = 110;
         if (gameframeVersion != 474) {
            Rasterizer2D.setClip(77, 0, 463, 0);
         } else {
            Rasterizer2D.setClip(screenMode2 + 115, localScreenMode + 0, localScreenMode + 463, screenMode2 + 0);
            shortCode = 210;
         }

         for (int chatMessageIndex = 0; chatMessageIndex < 100; chatMessageIndex++) {
            if (this.chatMessages[chatMessageIndex] != null) {
               int chatType = this.chatTypes[chatMessageIndex];
               int scalar7 = 70 - localChatContentHeight * 14 + this.chatScrollOffset;
               if (gameframeVersion == 474) {
                  scalar7 = 70 - scalar6 * 14 + this.chatScrollOffset - 2;
               }

               String text = this.chatNames[chatMessageIndex];
               int chatPrivilege = this.chatPrivileges[chatMessageIndex];
               int chatDonatorStatuse = this.chatDonatorStatuses[chatMessageIndex];
               int chatAccountMode = this.chatAccountModes[chatMessageIndex];
               if (chatType == 0 && (chatViewMode == 5 || chatViewMode == 0)) {
                  if (scalar7 > 0 && scalar7 < shortCode) {
                     if (hdChatbox && screenMode != 0) {
                        this.richPlainFont.drawBasicString(this.chatMessages[chatMessageIndex], localScreenMode + 4, scalar7 + screenMode2, 16777215, 0);
                     } else {
                        bitmapFont.textLeftShadow(false, localScreenMode + 4, 0, this.chatMessages[chatMessageIndex], scalar7 + screenMode2);
                     }
                  }

                  scalar6++;
                  localChatContentHeight++;
               }

               if ((chatType == 1 || chatType == 2 || chatType == 98)
                  && (chatType == 1 || this.publicChatMode == 0 || this.publicChatMode == 1 && this.isFriendOrSelf(text))
                  && (chatViewMode == 1 || chatViewMode == 0)) {
                  if (scalar7 > 0 && scalar7 < shortCode) {
                     int scalar8 = 4;
                     if (chatPrivilege == 1) {
                        this.moderatorIcons[0].drawBackground(localScreenMode + 4, scalar7 - 12 + screenMode2);
                        scalar8 += 14;
                        if (chatAccountMode != 0) {
                           this.gameModeIcons[chatAccountMode - 1].drawBackground(localScreenMode + 18, scalar7 - 12 + screenMode2);
                           scalar8 += 14;
                        }

                        if (chatDonatorStatuse == 1) {
                           this.moderatorIcons[2].drawBackground(scalar8 + localScreenMode, scalar7 - 12 + screenMode2);
                           scalar8 += 14;
                        }
                     } else if (chatPrivilege == 2) {
                        this.moderatorIcons[1].drawBackground(localScreenMode + 4, scalar7 - 12 + screenMode2);
                        scalar8 += 14;
                     } else if (chatPrivilege == 0) {
                        if (chatAccountMode != 0) {
                           this.gameModeIcons[chatAccountMode - 1].drawBackground(localScreenMode + 4, scalar7 - 12 + screenMode2);
                           scalar8 += 14;
                        }

                        if (chatDonatorStatuse == 1) {
                           this.moderatorIcons[2].drawBackground(scalar8 + localScreenMode, scalar7 - 12 + screenMode2);
                           scalar8 += 14;
                        }
                     }

                     if (chatType != 98) {
                        if (hdChatbox && screenMode != 0) {
                           bitmapFont.textLeftShadow(true, scalar8 + localScreenMode, 16777215, text + ":", scalar7 + screenMode2);
                           scalar8 += bitmapFont.getTextWidth(text) + 8;
                           bitmapFont.textLeftShadow(true, scalar8 + localScreenMode, 8366591, this.chatMessages[chatMessageIndex], scalar7 + screenMode2);
                        } else {
                           bitmapFont.textLeft(0, text + ":", scalar7 + screenMode2, scalar8 + localScreenMode);
                           scalar8 += bitmapFont.getTextWidth(text) + 8;
                           bitmapFont.textLeft(255, this.chatMessages[chatMessageIndex], scalar7 + screenMode2, scalar8 + localScreenMode);
                        }
                     } else {
                        bitmapFont.textLeft(this.MODELS, text + ":", scalar7 + screenMode2, scalar8 + localScreenMode);
                        scalar8 += bitmapFont.getTextWidth(text) + 8;
                        bitmapFont.textLeft(0, this.chatMessages[chatMessageIndex], scalar7 + screenMode2, scalar8 + localScreenMode);
                     }
                  }

                  scalar6++;
                  localChatContentHeight++;
               }

               if ((chatType == 3 || chatType == 7)
                  && (chatType == 7 || this.privateChatMode == 0 || this.privateChatMode == 1 && this.isFriendOrSelf(text))
                  && (chatViewMode == 2 || chatViewMode == 0 && this.splitpublicChat == 0)) {
                  if (scalar7 > 0 && scalar7 < shortCode) {
                     int chatPrivilege2 = 0;
                     if (hdChatbox && screenMode != 0) {
                        chatPrivilege2 = 16777215;
                     }

                     bitmapFont.textLeft(chatPrivilege2, "From", scalar7 + screenMode2, localScreenMode + 4);
                     int scalar9 = 4 + bitmapFont.getTextWidth("From ");
                     if (chatPrivilege == 1) {
                        this.moderatorIcons[0].drawBackground(scalar9 + localScreenMode, scalar7 - 12 + screenMode2);
                        scalar9 += 14;
                        if (chatAccountMode != 0) {
                           this.gameModeIcons[chatAccountMode - 1].drawBackground(scalar9 + localScreenMode, scalar7 - 12 + screenMode2);
                           scalar9 += 14;
                        }

                        if (chatDonatorStatuse == 1) {
                           this.moderatorIcons[2].drawBackground(scalar9 + localScreenMode, scalar7 - 12 + screenMode2);
                           scalar9 += 14;
                        }
                     } else if (chatPrivilege == 2) {
                        this.moderatorIcons[1].drawBackground(scalar9 + localScreenMode, scalar7 - 12 + screenMode2);
                        scalar9 += 14;
                     } else if (chatPrivilege == 0) {
                        if (chatAccountMode != 0) {
                           this.gameModeIcons[chatAccountMode - 1].drawBackground(scalar9 + localScreenMode, scalar7 - 12 + screenMode2);
                           scalar9 += 14;
                        }

                        if (chatDonatorStatuse == 1) {
                           this.moderatorIcons[2].drawBackground(scalar9 + localScreenMode, scalar7 - 12 + screenMode2);
                           scalar9 += 14;
                        }
                     }

                     bitmapFont.textLeft(chatPrivilege2, text + ":", scalar7 + screenMode2, scalar9 + localScreenMode);
                     scalar9 += bitmapFont.getTextWidth(text) + 8;
                     bitmapFont.textLeft(8388608, this.chatMessages[chatMessageIndex], scalar7 + screenMode2, scalar9 + localScreenMode);
                  }

                  scalar6++;
                  localChatContentHeight++;
               }

               if (chatType == 4 && (this.tradeMode == 0 || this.tradeMode == 1 && this.isFriendOrSelf(text)) && (chatViewMode == 3 || chatViewMode == 0)) {
                  if (scalar7 > 0 && scalar7 < shortCode) {
                     if (hdChatbox) {
                        ;
                     }

                     bitmapFont.textLeft(8388736, text + " " + this.chatMessages[chatMessageIndex], scalar7 + screenMode2, localScreenMode + 4);
                  }

                  scalar6++;
                  localChatContentHeight++;
               }

               if (chatType == 5 && this.privateChatMode < 2 && (chatViewMode == 2 || chatViewMode == 0 && this.splitpublicChat == 0)) {
                  if (scalar7 > 0 && scalar7 < shortCode) {
                     chatPrivilege = 8388608;
                     if (hdChatbox && screenMode != 0) {
                        chatPrivilege = 16732758;
                     }

                     bitmapFont.textLeft(chatPrivilege, this.chatMessages[chatMessageIndex], scalar7 + screenMode2, localScreenMode + 4);
                  }

                  scalar6++;
                  localChatContentHeight++;
               }

               if (chatType == 6 && this.privateChatMode < 2 && (chatViewMode == 2 || chatViewMode == 0 && this.splitpublicChat == 0)) {
                  if (scalar7 > 0 && scalar7 < shortCode) {
                     chatPrivilege = 0;
                     int chatPrivilege3 = 8388608;
                     if (hdChatbox && screenMode != 0) {
                        chatPrivilege = 16777215;
                        chatPrivilege3 = 16732758;
                     }

                     bitmapFont.textLeft(chatPrivilege, "To " + text + ":", scalar7 + screenMode2, localScreenMode + 4);
                     bitmapFont.textLeft(chatPrivilege3, this.chatMessages[chatMessageIndex], scalar7 + screenMode2, 12 + bitmapFont.getTextWidth("To " + text) + localScreenMode);
                  }

                  scalar6++;
                  localChatContentHeight++;
               }

               if ((chatType == 255 || chatType == 8 && (this.tradeMode == 0 || this.tradeMode == 1 && this.isFriendOrSelf(text)))
                  && (chatViewMode == 3 || chatViewMode == 0)) {
                  if (scalar7 > 0 && scalar7 < shortCode) {
                     bitmapFont.textLeft(8270336, text + " " + this.chatMessages[chatMessageIndex], scalar7 + screenMode2, localScreenMode + 4);
                  }

                  scalar6++;
                  localChatContentHeight++;
               }
            }
         }

         Rasterizer2D.resetClip();
         if (gameframeVersion != 474) {
            this.chatContentHeight = localChatContentHeight * 14 + 7;
            if (this.chatContentHeight < 78) {
               this.chatContentHeight = 78;
            }

            this.drawScrollBar(77, this.chatContentHeight - this.chatScrollOffset - 77, 0, 463, this.chatContentHeight, false);
         } else {
            this.chatContentHeight = localChatContentHeight * 14 + 7 - 10;
            if (this.chatContentHeight < 111) {
               this.chatContentHeight = 111;
            }

            boolean flag = false;
            if (hdChatbox && screenMode != 0) {
               flag = true;
            }

            this.drawScrollBar(114, this.chatContentHeight - this.chatScrollOffset - 113, screenMode2 + 0, localScreenMode + 489, this.chatContentHeight, flag);
         }

         byte byteCode2 = 0;
         byte byteCode3 = 78;
         if (gameframeVersion == 474) {
            byteCode3 = 114;
         }

         String localText;
         if (localPlayer != null && localPlayer.name != null) {
            localText = localPlayer.name;
         } else {
            localText = NameUtils.formatDisplayName(username);
         }

         if (this.yCameraCurve == 1) {
            this.moderatorIcons[0].drawBackground(localScreenMode + 4, byteCode3 + screenMode2);
            byteCode2 = 14;
            if (this.xCameraCurve != 0) {
               this.gameModeIcons[this.xCameraCurve - 1].drawBackground(localScreenMode + 18, byteCode3 + screenMode2);
               byteCode2 += 14;
            }

            if (this.myPrivilege == 1) {
               this.moderatorIcons[2].drawBackground(byteCode2 + 4 + localScreenMode, byteCode3 + screenMode2);
               byteCode2 += 14;
            }
         } else if (this.yCameraCurve == 2) {
            this.moderatorIcons[1].drawBackground(localScreenMode + 4, byteCode3 + screenMode2);
            byteCode2 = 14;
         } else if (this.yCameraCurve == 0) {
            if (this.xCameraCurve != 0) {
               this.gameModeIcons[this.xCameraCurve - 1].drawBackground(localScreenMode + 4, byteCode3 + screenMode2);
               byteCode2 += 14;
            }

            if (this.myPrivilege == 1) {
               this.moderatorIcons[2].drawBackground(byteCode2 + 4 + localScreenMode, byteCode3 + screenMode2);
               byteCode2 += 14;
            }
         }

         if (gameframeVersion != 474) {
            bitmapFont.textLeft(0, localText + ":", screenMode2 + 90, byteCode2 + 4 + localScreenMode);
            bitmapFont.textLeft(255, this.inputString + "*", screenMode2 + 90, 6 + bitmapFont.getTextWidth(localText + ": ") + byteCode2 + localScreenMode);
            Rasterizer2D.drawHorizontalLine(77, 0, classicChatRasterWidth, 0);
         } else if (hdChatbox && screenMode != 0) {
            bitmapFont.textLeftShadow(true, byteCode2 + 4 + localScreenMode, 16777215, localText + ": ", screenMode2 + 126);
            bitmapFont.textLeftShadow(8366591, this.inputString + "*", screenMode2 + 126, 6 + bitmapFont.getTextWidth(localText + ": ") + byteCode2 + localScreenMode, true);
            Rasterizer2D.drawHorizontalLine(screenMode2 + 114, 7170647, 505, localScreenMode + 0);
            Rasterizer2D.drawHorizontalGradient(localScreenMode + 0, screenMode2 + -3, 405, 7170647, 256);
         } else {
            bitmapFont.textLeft(0, localText + ":", screenMode2 + 126, byteCode2 + 4 + localScreenMode);
            bitmapFont.textLeft(255, this.inputString + "*", screenMode2 + 126, 6 + bitmapFont.getTextWidth(localText + ": ") + byteCode2 + localScreenMode);
            Rasterizer2D.drawHorizontalLine(screenMode2 + 114, 8418912, 506, localScreenMode + 0);
         }
      }

      if (this.menuOpen && this.menuScreenArea == 2) {
         this.drawMenu();
      }

      if (screenMode == 0) {
         if (gameframeVersion != 474) {
            this.chatboxImageProducer.drawToBuffer(357, this.frameBuffer, 17);
         } else {
            this.chatboxImageProducer.drawToBuffer(345, this.frameBuffer, 7);
         }
      }

      this.gameScreenImageProducer.initDrawingArea();
      Rasterizer3D.scanOffsets = viewportScanOffsets;
   }

   @Override
   public void init() {
      try {
         nodeID = 10;
         portOff = 0;
         setHighMemoryMode();
         isMembers = true;
         SignLink.storeId = 32;
         SignLink.startpriv(InetAddress.getLocalHost());
         short shortCode = 765;
         shortCode = 503;
         Client client = this;
         super.clientWindow = ClientWindow.getInstance();
         client.canvasWidth = 765;
         client.canvasHeight = 503;
         client.graphics = client.getGameComponent().getGraphics();
         int canvasWidth = client.canvasWidth;
         int canvasHeight = client.canvasHeight;
         client.getGameComponent();
         client.graphicsBuffer = new BufferedImageGraphicsBuffer(canvasWidth, canvasHeight);
         client.startRunnable(client, 1);
         clientInstance = this;

         try {
            loadDisplaySettings();
         } catch (IOException exception) {
         }

         try {
            loadUserConfig();
         } catch (IOException exception3) {
         }
      } catch (Exception exception2) {
      }
   }
   @Override
   public final void startRunnable(Runnable runnable, int scalarArgument) {
      if (scalarArgument > 10) {
         scalarArgument = 10;
      }

      super.startRunnable(runnable, scalarArgument);
   }
   public final Socket openSocket(int scalarArgument) throws IOException {
      return new Socket(InetAddress.getByName(this.getCodeBase().getHost()), scalarArgument);
   }
   private boolean processMenuClick() {
      if (this.activeInterfaceType != 0) {
         return false;
      }

      if (this.runOrbHovered && super.clickButton == 1) {
         this.processMenuActions(1);
         return true;
      }

      int clickButton = super.clickButton;
      if (this.spellSelected == 1 && super.clickX >= 516 && super.clickY >= 160 && super.clickX <= 765 && super.clickY <= 205) {
         clickButton = 0;
      }

      if (this.menuOpen) {
         if (clickButton != 1) {
            int mouseX = super.mouseX;
            int mouseY = super.mouseY;
            if (this.menuScreenArea == 0) {
               mouseX -= screenMode == 0 ? 4 : 0;
               mouseY -= screenMode == 0 ? 4 : 0;
            }

            if (this.menuScreenArea == 1) {
               mouseX -= 553;
               mouseY -= 205;
            }

            if (this.menuScreenArea == 2) {
               if (gameframeVersion != 474) {
                  mouseX -= 17;
                  mouseY -= 357;
               } else {
                  mouseX -= 7;
                  mouseY -= 345;
                  if (mouseY > 141) {
                     mouseY = 141;
                  }
               }
            }

            if (mouseX < this.menuOffsetX - 10
               || mouseX > this.menuOffsetX + this.menuWidth + 10
               || mouseY < this.menuOffsetY - 10
               || mouseY > this.menuOffsetY + this.menuHeight + 10) {
               this.menuOpen = false;
               if (this.menuScreenArea == 1) {
                  this.needDrawTabArea = true;
               }

               if (this.menuScreenArea == 2) {
                  this.inputTaken = true;
               }
            }
         }

         if (clickButton == 1) {
            int menuOffsetX = this.menuOffsetX;
            int menuOffsetY = this.menuOffsetY;
            int menuWidth = this.menuWidth;
            int clickX = this.getMenuClickX();
            int clickY = this.getMenuClickY();
            if (this.menuScreenArea == 0) {
               clickX -= screenMode == 0 ? 4 : 0;
               clickY -= screenMode == 0 ? 4 : 0;
            }

            if (this.menuScreenArea == 1) {
               clickX -= 553;
               clickY -= 205;
            }

            if (this.menuScreenArea == 2) {
               if (gameframeVersion != 474) {
                  clickX -= 17;
                  clickY -= 357;
               } else {
                  clickX -= 7;
                  clickY -= 345;
               }
            }

            clickButton = -1;

            for (int sourceClickButton = 0; sourceClickButton < this.menuActionCount; sourceClickButton++) {
               int scalar = menuOffsetY + 31 + (this.menuActionCount - 1 - sourceClickButton) * 15;
               if (clickX > menuOffsetX && clickX < menuOffsetX + menuWidth && clickY > scalar - 13 && clickY < scalar + 3) {
                  clickButton = sourceClickButton;
               }
            }

            if (clickButton != -1) {
               this.processMenuActions(clickButton);
            }

            this.menuOpen = false;
            if (this.menuScreenArea == 1) {
               this.needDrawTabArea = true;
            }

            if (this.menuScreenArea == 2) {
               this.inputTaken = true;
            }
         }

         return true;
      } else {
         if (clickButton == 1 && this.menuActionCount > 0) {
            int menuActionIdIndex;
            if (priorityMenuActionIndex != -1) {
               menuActionIdIndex = priorityMenuActionIndex;
            } else {
               menuActionIdIndex = this.menuActionCount - 1;
            }

            int localMenuActionIds;
            if ((localMenuActionIds = this.menuActionIds[menuActionIdIndex]) == 632
               || localMenuActionIds == 78
               || localMenuActionIds == 867
               || localMenuActionIds == 431
               || localMenuActionIds == 53
               || localMenuActionIds == 74
               || localMenuActionIds == 454
               || localMenuActionIds == 539
               || localMenuActionIds == 493
               || localMenuActionIds == 847
               || localMenuActionIds == 447
               || localMenuActionIds == 1125) {
               int draggedSlotOrMenuParam0 = this.menuParam0[menuActionIdIndex];
               int menuParam1Entry = this.menuParam1[menuActionIdIndex];
               Widget widget;
               if ((widget = Widget.widgets[menuParam1Entry]).swapItems || widget.replaceItems) {
                  this.widgetDragThresholdExceeded = false;
                  this.widgetDragDuration = 0;
                  this.draggedWidgetId = menuParam1Entry;
                  this.draggedSlot = draggedSlotOrMenuParam0;
                  this.activeInterfaceType = 2;
                  this.dragStartX = super.clickX;
                  this.dragStartY = super.clickY;
                  if (Widget.widgets[menuParam1Entry].parentId == this.openInterfaceId) {
                     this.activeInterfaceType = 1;
                  }

                  if (Widget.widgets[menuParam1Entry].parentId == this.backDialogID) {
                     this.activeInterfaceType = 3;
                  }

                  return true;
               }
            }
         }

         if (clickButton == 1 && (this.oneButtonMouse == 1 || this.isAddFriendMenuAction(this.menuActionCount - 1)) && this.menuActionCount > 2) {
            clickButton = 2;
         }

         if (clickButton == 1 && this.menuActionCount > 0) {
            int menuActionIdIndex2;
            if (priorityMenuActionIndex != -1) {
               menuActionIdIndex2 = priorityMenuActionIndex;
            } else {
               menuActionIdIndex2 = this.menuActionCount - 1;
            }

            this.processMenuActions(menuActionIdIndex2);
         }

         if (clickButton == 2 && this.menuActionCount > 0) {
            this.determineMenuSize();
         }

         return false;
      }
   }
   private void rebuildWorldRegion() {
      try {
         this.lastRenderedPlane = -1;
         this.incompleteAnimables.removeAll();
         this.projectiles.removeAll();
         Rasterizer3D.clearTextureCache();
         clearModelCaches();
         this.scene.initToNull();
         System.gc();

         for (int collisionMapIndex = 0; collisionMapIndex < 4; collisionMapIndex++) {
            this.collisionMaps[collisionMapIndex].reset();
         }

         for (int byteGroundArrayIndex = 0; byteGroundArrayIndex < 4; byteGroundArrayIndex++) {
            for (int loopIndex = 0; loopIndex < 104; loopIndex++) {
               for (int loopIndex2 = 0; loopIndex2 < 104; loopIndex2++) {
                  this.byteGroundArray[byteGroundArrayIndex][loopIndex][loopIndex2] = 0;
               }
            }
         }

         RegionBuilder regionBuilder = new RegionBuilder(this.byteGroundArray, this.intGroundArray);
         int terrainRegionDataLengthOrTerrainRegionData = this.terrainRegionData.length;
         this.outgoingBuffer.writeOpcode(0);
         if (!this.constructedViewport) {
            for (int regionIdIndex = 0; regionIdIndex < terrainRegionDataLengthOrTerrainRegionData; regionIdIndex++) {
               int scalar = (this.regionIds[regionIdIndex] >> 8 << 6) - this.baseX;
               int scalar2 = ((this.regionIds[regionIdIndex] & 0xFF) << 6) - this.baseY;
               byte[] localTerrainRegionData;
               if ((localTerrainRegionData = this.terrainRegionData[regionIdIndex]) != null) {
                  regionBuilder.loadTerrainRegion(localTerrainRegionData, scalar2, scalar, this.mapRegionX - 6 << 3, this.mapRegionY - 6 << 3, this.collisionMaps);
               }
            }

            for (int regionIdIndex2 = 0; regionIdIndex2 < terrainRegionDataLengthOrTerrainRegionData; regionIdIndex2++) {
               int scalar3 = (this.regionIds[regionIdIndex2] >> 8 << 6) - this.baseX;
               int scalar4 = ((this.regionIds[regionIdIndex2] & 0xFF) << 6) - this.baseY;
               byte[] terrainRegionData2;
               if ((terrainRegionData2 = this.terrainRegionData[regionIdIndex2]) == null && this.mapRegionY < 800) {
                  regionBuilder.clearChunk(scalar4, 64, 64, scalar3);
               }
            }

            if (++regionLoadCounter > 160) {
               regionLoadCounter = 0;
               this.outgoingBuffer.writeOpcode(238);
               this.outgoingBuffer.writeByte(96);
            }

            this.outgoingBuffer.writeOpcode(0);

            for (int objectRegionDataIndex = 0; objectRegionDataIndex < terrainRegionDataLengthOrTerrainRegionData; objectRegionDataIndex++) {
               byte[] localObjectRegionData;
               if ((localObjectRegionData = this.objectRegionData[objectRegionDataIndex]) != null) {
                  int scalar5 = (this.regionIds[objectRegionDataIndex] >> 8 << 6) - this.baseX;
                  int scalar6 = ((this.regionIds[objectRegionDataIndex] & 0xFF) << 6) - this.baseY;
                  regionBuilder.loadObjectsRegion(scalar5, this.collisionMaps, scalar6, this.scene, localObjectRegionData);
               }
            }
         }

         if (this.constructedViewport) {
            for (int instanceChunkTemplateIndex = 0; instanceChunkTemplateIndex < 4; instanceChunkTemplateIndex++) {
               for (int loopIndex3 = 0; loopIndex3 < 13; loopIndex3++) {
                  for (int loopIndex4 = 0; loopIndex4 < 13; loopIndex4++) {
                     int localInstanceChunkTemplates;
                     if ((localInstanceChunkTemplates = this.instanceChunkTemplates[instanceChunkTemplateIndex][loopIndex3][loopIndex4]) != -1) {
                        int scalar7 = localInstanceChunkTemplates >> 24 & 3;
                        int scalar8 = localInstanceChunkTemplates >> 1 & 3;
                        int scalar9 = localInstanceChunkTemplates >> 14 & 1023;
                        int scalar10 = localInstanceChunkTemplates >> 3 & 2047;
                        terrainRegionDataLengthOrTerrainRegionData = (scalar9 / 8 << 8) + scalar10 / 8;

                        for (int regionIdIndex3 = 0; regionIdIndex3 < this.regionIds.length; regionIdIndex3++) {
                           if (this.regionIds[regionIdIndex3] == terrainRegionDataLengthOrTerrainRegionData && this.terrainRegionData[regionIdIndex3] != null) {
                              regionBuilder.loadTerrainChunk(
                                 scalar7, scalar8, this.collisionMaps, loopIndex3 << 3, (scalar9 & 7) << 3, this.terrainRegionData[regionIdIndex3], (scalar10 & 7) << 3, instanceChunkTemplateIndex, loopIndex4 << 3
                              );
                              break;
                           }
                        }
                     }
                  }
               }
            }

            for (int loopIndex5 = 0; loopIndex5 < 13; loopIndex5++) {
               for (int loopIndex6 = 0; loopIndex6 < 13; loopIndex6++) {
                  if (this.instanceChunkTemplates[0][loopIndex5][loopIndex6] == -1) {
                     regionBuilder.clearChunk(loopIndex6 << 3, 8, 8, loopIndex5 << 3);
                  }
               }
            }

            this.outgoingBuffer.writeOpcode(0);

            for (int instanceChunkTemplateIndex2 = 0; instanceChunkTemplateIndex2 < 4; instanceChunkTemplateIndex2++) {
               for (int loopIndex7 = 0; loopIndex7 < 13; loopIndex7++) {
                  for (int loopIndex8 = 0; loopIndex8 < 13; loopIndex8++) {
                     int instanceChunkTemplates2;
                     if ((instanceChunkTemplates2 = this.instanceChunkTemplates[instanceChunkTemplateIndex2][loopIndex7][loopIndex8]) != -1) {
                        int scalar11 = instanceChunkTemplates2 >> 24 & 3;
                        int scalar12 = instanceChunkTemplates2 >> 1 & 3;
                        int scalar13 = instanceChunkTemplates2 >> 14 & 1023;
                        int scalar14 = instanceChunkTemplates2 >> 3 & 2047;
                        terrainRegionDataLengthOrTerrainRegionData = (scalar13 / 8 << 8) + scalar14 / 8;

                        for (int regionIdIndex4 = 0; regionIdIndex4 < this.regionIds.length; regionIdIndex4++) {
                           if (this.regionIds[regionIdIndex4] == terrainRegionDataLengthOrTerrainRegionData && this.objectRegionData[regionIdIndex4] != null) {
                              regionBuilder.loadObjectChunk(
                                 this.collisionMaps,
                                 this.scene,
                                 scalar11,
                                 loopIndex7 << 3,
                                 (scalar14 & 7) << 3,
                                 instanceChunkTemplateIndex2,
                                 this.objectRegionData[regionIdIndex4],
                                 (scalar13 & 7) << 3,
                                 scalar12,
                                 loopIndex8 << 3
                              );
                              break;
                           }
                        }
                     }
                  }
               }
            }
         }

         this.outgoingBuffer.writeOpcode(0);
         regionBuilder.buildScene(this.collisionMaps, this.scene);
         this.gameScreenImageProducer.initDrawingArea();
         this.outgoingBuffer.writeOpcode(0);
         int minimumPlane = RegionBuilder.minimumPlane;
         if (RegionBuilder.minimumPlane > this.plane) {
            minimumPlane = this.plane;
         }

         if (minimumPlane < this.plane - 1) {
            ;
         }

         this.scene.setPlane(0);

         for (int loopIndex9 = 0; loopIndex9 < 104; loopIndex9++) {
            for (int loopIndex10 = 0; loopIndex10 < 104; loopIndex10++) {
               this.spawnGroundItem(loopIndex9, loopIndex10);
            }
         }

         if (++regionBuildNoiseCounter > 98) {
            regionBuildNoiseCounter = 0;
            this.outgoingBuffer.writeOpcode(150);
         }

         Client client = this;

         for (SpawnedObject spawnedObject = (SpawnedObject)this.spawns.first(); spawnedObject != null; spawnedObject = (SpawnedObject)client.spawns.next()) {
            if (spawnedObject.longevity == -1) {
               spawnedObject.delay = 0;
               client.captureSpawnedObjectState(spawnedObject);
            } else {
               spawnedObject.unlink();
            }
         }
      } catch (Exception exception) {
         System.err.println("Region build failed before terrain finalization:");
         exception.printStackTrace();
      }

      ObjectDefinition.rawModelCache.clear();
      if (super.gameFrame != null) {
         this.outgoingBuffer.writeOpcode(210);
         this.outgoingBuffer.writeInt(1057001181);
      }

      System.gc();
      Rasterizer3D.initializeTextureCache();
      this.onDemandFetcher.clearExtraRequests();
      int position = (this.mapRegionX - 6) / 8 - 1;
      int loopIndex11 = (this.mapRegionX + 6) / 8 + 1;
      int position2 = (this.mapRegionY - 6) / 8 - 1;
      int loopIndex12 = (this.mapRegionY + 6) / 8 + 1;
      if (this.inPlayerOwnedHouse) {
         position = 49;
         loopIndex11 = 50;
         position2 = 49;
         loopIndex12 = 50;
      }

      for (int loopIndex13 = position; loopIndex13 <= loopIndex11; loopIndex13++) {
         for (int loopIndex14 = position2; loopIndex14 <= loopIndex12; loopIndex14++) {
            if (loopIndex13 == position || loopIndex13 == loopIndex11 || loopIndex14 == position2 || loopIndex14 == loopIndex12) {
               int localOnDemandFetcher;
               if ((localOnDemandFetcher = this.onDemandFetcher.getMapFileId(0, loopIndex14, loopIndex13)) != -1) {
                  this.onDemandFetcher.queueExtraRequest(localOnDemandFetcher, 3);
               }

               int onDemandFetcher2;
               if ((onDemandFetcher2 = this.onDemandFetcher.getMapFileId(1, loopIndex14, loopIndex13)) != -1) {
                  this.onDemandFetcher.queueExtraRequest(onDemandFetcher2, 3);
               }
            }
         }
      }
   }
   private static void clearModelCaches() {
      ObjectDefinition.rawModelCache.clear();
      ObjectDefinition.modelCache.clear();
      NpcDefinition.modelCache.clear();
      ItemDefinition.modelCache.clear();
      ItemDefinition.spriteCache.clear();
      Player.appearanceModelCache.clear();
      SpotAnimationDefinition.modelCache.clear();
   }
   private void refreshMinimap(int byteGroundArrayIndex) {
      int[] pixels = this.minimapImage.pixels;
      int pixelsLength = this.minimapImage.pixels.length;

      for (int pixelIndex = 0; pixelIndex < pixelsLength; pixelIndex++) {
         pixels[pixelIndex] = 0;
      }

      for (int tileRotationMapIndex = 1; tileRotationMapIndex < 103; tileRotationMapIndex++) {
         pixelsLength = 24628 + (103 - tileRotationMapIndex << 9 << 2);

         for (int tileIndex = 1; tileIndex < 103; tileIndex++) {
            if ((this.byteGroundArray[byteGroundArrayIndex][tileIndex][tileRotationMapIndex] & 24) == 0) {
               this.scene.drawMinimapTile(pixels, pixelsLength, byteGroundArrayIndex, tileIndex, tileRotationMapIndex);
            }

            if (byteGroundArrayIndex < 3 && (this.byteGroundArray[byteGroundArrayIndex + 1][tileIndex][tileRotationMapIndex] & 8) != 0) {
               this.scene.drawMinimapTile(pixels, pixelsLength, byteGroundArrayIndex + 1, tileIndex, tileRotationMapIndex);
            }

            pixelsLength += 4;
         }
      }

      int sourceTileY = (238 + (int)(Math.random() * 20.0) - 10 << 16) + (238 + (int)(Math.random() * 20.0) - 10 << 8) + 238 + (int)(Math.random() * 20.0) - 10;
      pixelsLength = 238 + (int)(Math.random() * 20.0) - 10 << 16;
      Sprite sprite = this.minimapImage;
      Rasterizer2D.setRasterBuffer(this.minimapImage.spriteHeight, sprite.spriteWidth, sprite.pixels, null);

      for (int tileY2 = 1; tileY2 < 103; tileY2++) {
         for (int clippingDataIndex = 1; clippingDataIndex < 103; clippingDataIndex++) {
            if ((this.byteGroundArray[byteGroundArrayIndex][clippingDataIndex][tileY2] & 24) == 0) {
               this.drawMapScenes(tileY2, sourceTileY, clippingDataIndex, pixelsLength, byteGroundArrayIndex);
            }

            if (byteGroundArrayIndex < 3 && (this.byteGroundArray[byteGroundArrayIndex + 1][clippingDataIndex][tileY2] & 8) != 0) {
               this.drawMapScenes(tileY2, sourceTileY, clippingDataIndex, pixelsLength, byteGroundArrayIndex + 1);
            }
         }
      }

      this.gameScreenImageProducer.initDrawingArea();
      this.mapFunctionCount = 0;

      for (int sourcePixelsLength = 0; sourcePixelsLength < 104; sourcePixelsLength++) {
         for (int tileY = 0; tileY < 104; tileY++) {
            if ((byteGroundArrayIndex = this.scene.getFloorDecorationHash(this.plane, sourcePixelsLength, tileY)) != 0 && (byteGroundArrayIndex = ObjectDefinition.lookup(byteGroundArrayIndex >> 14 & 32767).icon) >= 0) {
               pixelsLength = sourcePixelsLength;
               sourceTileY = tileY;
               if (byteGroundArrayIndex != 22 && byteGroundArrayIndex != 29 && byteGroundArrayIndex != 34 && byteGroundArrayIndex != 36 && byteGroundArrayIndex != 46 && byteGroundArrayIndex != 47 && byteGroundArrayIndex != 48) {
                  int[][] clippingData = this.collisionMaps[this.plane].clippingData;

                  for (int loopIndex = 0; loopIndex < 10; loopIndex++) {
                     int scalar;
                     if ((scalar = (int)(Math.random() * 4.0)) == 0 && pixelsLength > 0 && pixelsLength > sourcePixelsLength - 3 && (clippingData[pixelsLength - 1][sourceTileY] & 19398920) == 0) {
                        pixelsLength--;
                     }

                     if (scalar == 1 && pixelsLength < 103 && pixelsLength < sourcePixelsLength + 3 && (clippingData[pixelsLength + 1][sourceTileY] & 19399040) == 0) {
                        pixelsLength++;
                     }

                     if (scalar == 2 && sourceTileY > 0 && sourceTileY > tileY - 3 && (clippingData[pixelsLength][sourceTileY - 1] & 19398914) == 0) {
                        sourceTileY--;
                     }

                     if (scalar == 3 && sourceTileY < 103 && sourceTileY < tileY + 3 && (clippingData[pixelsLength][sourceTileY + 1] & 19398944) == 0) {
                        sourceTileY++;
                     }
                  }
               }

               this.minimapHint[this.mapFunctionCount] = this.mapFunctions[byteGroundArrayIndex];
               this.minimapHintX[this.mapFunctionCount] = pixelsLength;
               this.minimapHintY[this.mapFunctionCount] = sourceTileY;
               this.mapFunctionCount++;
            }
         }
      }
   }
   private void spawnGroundItem(int scalarArgument, int scalarArgument2) {
      NodeDeque nodeDeque;
      if ((nodeDeque = this.groundItems[this.plane][scalarArgument][scalarArgument2]) == null) {
         this.scene.removeGroundItems(this.plane, scalarArgument, scalarArgument2);
      } else {
         int scalar = -99999999;
         GroundItem groundItem = null;

         for (GroundItem sourceGroundItem = (GroundItem)nodeDeque.first(); sourceGroundItem != null; sourceGroundItem = (GroundItem)nodeDeque.next()) {
            ItemDefinition itemDefinition;
            int scalar2 = (itemDefinition = ItemDefinition.lookup(sourceGroundItem.id)).value;
            if (itemDefinition.stackable) {
               scalar2 *= sourceGroundItem.quantity + 1;
            }

            if (scalar2 > scalar) {
               scalar = scalar2;
               groundItem = sourceGroundItem;
            }
         }

         nodeDeque.addFirst(groundItem);
         GroundItem groundItem2 = null;
         GroundItem groundItem3 = null;

         for (GroundItem groundItem4 = (GroundItem)nodeDeque.first(); groundItem4 != null; groundItem4 = (GroundItem)nodeDeque.next()) {
            if (groundItem4.id != groundItem.id && groundItem2 == null) {
               groundItem2 = groundItem4;
            }

            if (groundItem4.id != groundItem.id && groundItem4.id != groundItem2.id && groundItem3 == null) {
               groundItem3 = groundItem4;
            }
         }

         int scalar3 = scalarArgument + (scalarArgument2 << 7) + 1610612736;
         this.scene.addGroundItemTile(scalarArgument, scalar3, groundItem2, this.getTileHeight(this.plane, (scalarArgument2 << 7) + 64, (scalarArgument << 7) + 64), groundItem3, groundItem, this.plane, scalarArgument2);
      }
   }
   private void showNPCs(boolean flag) {
      for (int npcIndex = 0; npcIndex < this.npcCount; npcIndex++) {
         Npc npc = this.npcs[this.npcIndices[npcIndex]];
         int scalar = 536870912 + (this.npcIndices[npcIndex] << 14);
         if (npc != null && npc.isVisible() && npc.definition.priorityRender == flag) {
            int tileCycleMarkerIndex = npc.worldX >> 7;
            int localWorldY = npc.worldY >> 7;
            if (tileCycleMarkerIndex >= 0 && tileCycleMarkerIndex < 104 && localWorldY >= 0 && localWorldY < 104) {
               if (npc.size == 1 && (npc.worldX & 127) == 64 && (npc.worldY & 127) == 64) {
                  if (this.tileCycleMarkers[tileCycleMarkerIndex][localWorldY] == this.sceneCycle) {
                     continue;
                  }

                  this.tileCycleMarkers[tileCycleMarkerIndex][localWorldY] = this.sceneCycle;
               }

               if (!npc.definition.clickable) {
                  scalar -= Integer.MIN_VALUE;
               }

               this.scene
                  .addEntityWithRadius(
                     this.plane, npc.orientation, this.getTileHeight(this.plane, npc.worldY, npc.worldX), scalar, npc.worldY, (npc.size - 1 << 6) + 60, npc.worldX, npc, npc.animationStretches
                  );
            }
         }
      }
   }
   static final void skipPcmSamples(int scalarArgument) {
      if (pcmStream != null) {
         pcmStream.skip(256);
      }

      advanceAudioTiming(256);
   }
   private void showOnDemandLoadError() {
      String text = "ondemand";
      System.out.println(text);

      try {
         this.getAppletContext().showDocument(new URL(this.getCodeBase(), "loaderror_" + text + ".html"));
      } catch (Exception exception) {
         exception.printStackTrace();
      }

      while (true) {
         try {
            Thread.sleep(1000L);
         } catch (Exception exception2) {
         }
      }
   }
   private void buildInterfaceMenu(int scalarArgument, Widget widget, int mouseX, int scalarArgument2, int mouseY, int scrollPosition) {
      if (widget.type == 0
         && widget.childIds != null
         && (!widget.hoverOnly || widget.id == this.hoveredWidgetId)
         && mouseX >= scalarArgument
         && mouseY >= scalarArgument2
         && mouseX <= scalarArgument + widget.width
         && mouseY <= scalarArgument2 + widget.height) {
         int childIdsLengthOrChildIds = widget.childIds.length;

         for (int childXIndex = 0; childXIndex < childIdsLengthOrChildIds; childXIndex++) {
            int localChildX = widget.childX[childXIndex] + scalarArgument;
            int localChildY = widget.childY[childXIndex] + scalarArgument2 - scrollPosition;
            Widget widget3;
            if (!(widget3 = Widget.widgets[widget.childIds[childXIndex]]).hoverOnly || this.hoveredWidgetId == widget3.id) {
               localChildX += widget3.runtimeXOffset;
               localChildY += widget3.runtimeYOffset;
               if ((widget3.hoverId >= 0 || widget3.defaultHoverColor != 0) && mouseX >= localChildX && mouseY >= localChildY && mouseX < localChildX + widget3.width && mouseY < localChildY + widget3.height) {
                  if (widget3.hoverId >= 0) {
                     this.hoveredWidgetId = widget3.hoverId;
                  } else {
                     this.hoveredWidgetId = widget3.id;
                  }
               }

               if (widget3.type == 8 && mouseX >= localChildX && mouseY >= localChildY && mouseX < localChildX + widget3.width && mouseY < localChildY + widget3.height) {
                  this.hoveredTooltipWidgetId = widget3.id;
               }

               if (widget3.type == 0) {
                  this.buildInterfaceMenu(localChildX, widget3, mouseX, localChildY, mouseY, widget3.scrollPosition);
                  if (widget3.scrollMax > widget3.height) {
                     this.handleScrollbar(localChildX + widget3.width, widget3.height, mouseX, mouseY, widget3, localChildY, true, widget3.scrollMax);
                  }
               } else {
                  if (widget3.optionType == 1 && mouseX >= localChildX && mouseY >= localChildY && mouseX < localChildX + widget3.width && mouseY < localChildY + widget3.height) {
                     boolean flag = false;
                     if (widget3.contentType != 0) {
                        Widget widget2 = widget3;
                        Client client = this;
                        int contentType = widget2.contentType;
                        boolean sourceFlag;
                        if ((widget2.contentType <= 0 || contentType > 200) && (contentType < 701 || contentType > 900)) {
                           if (contentType >= 401 && contentType <= 500) {
                              client.menuActionNames[client.menuActionCount] = "Remove @whi@" + widget2.message;
                              client.menuActionIds[client.menuActionCount] = 322;
                              client.menuActionCount++;
                              sourceFlag = true;
                           } else {
                              sourceFlag = false;
                           }
                        } else {
                           if (contentType >= 801) {
                              contentType -= 701;
                           } else if (contentType >= 701) {
                              contentType -= 601;
                           } else if (contentType >= 101) {
                              contentType -= 101;
                           } else {
                              contentType--;
                           }

                           client.menuActionNames[client.menuActionCount] = "Remove @whi@" + client.friendNames[contentType];
                           client.menuActionIds[client.menuActionCount] = 792;
                           client.menuActionCount++;
                           client.menuActionNames[client.menuActionCount] = "Message @whi@" + client.friendNames[contentType];
                           client.menuActionIds[client.menuActionCount] = 639;
                           client.menuActionCount++;
                           sourceFlag = true;
                        }

                        flag = sourceFlag;
                     }

                     if (!flag && (widget3.id != 5985 || this.yCameraCurve > 0)) {
                        if (widget3.actions != null) {
                           for (int actionIndex = widget3.actions.length - 1; actionIndex >= 0; actionIndex--) {
                              String text;
                              if ((text = widget3.actions[actionIndex]) != null) {
                                 this.menuActionNames[this.menuActionCount] = text;
                                 this.menuActionIds[this.menuActionCount] = 222;
                                 this.menuParam1[this.menuActionCount] = widget3.id;
                                 this.hoveredMenuActionIndex = this.menuActionCount++;
                              }
                           }
                        }

                        this.menuActionNames[this.menuActionCount] = widget3.tooltip;
                        this.menuActionIds[this.menuActionCount] = 315;
                        this.menuParam1[this.menuActionCount] = widget3.id;
                        this.menuActionCount++;
                     }
                  }

                  if (widget3.optionType == 2 && this.spellSelected == 0 && mouseX >= localChildX && mouseY >= localChildY && mouseX < localChildX + widget3.width && mouseY < localChildY + widget3.height
                     )
                   {
                     String selectedActionName = widget3.selectedActionName;
                     if (widget3.selectedActionName.indexOf(" ") != -1) {
                        selectedActionName = selectedActionName.substring(0, selectedActionName.indexOf(" "));
                     }

                     this.menuActionNames[this.menuActionCount] = selectedActionName + " @gre@" + widget3.spellName;
                     this.menuActionIds[this.menuActionCount] = 626;
                     this.menuParam1[this.menuActionCount] = widget3.id;
                     this.menuActionCount++;
                  }

                  if (widget3.optionType == 3 && mouseX >= localChildX && mouseY >= localChildY && mouseX < localChildX + widget3.width && mouseY < localChildY + widget3.height) {
                     this.menuActionNames[this.menuActionCount] = "Close";
                     this.menuActionIds[this.menuActionCount] = 200;
                     this.menuParam1[this.menuActionCount] = widget3.id;
                     this.menuActionCount++;
                  }

                  if (widget3.optionType == 4 && mouseX >= localChildX && mouseY >= localChildY && mouseX < localChildX + widget3.width && mouseY < localChildY + widget3.height) {
                     this.menuActionNames[this.menuActionCount] = widget3.tooltip;
                     this.menuActionIds[this.menuActionCount] = 169;
                     this.menuParam1[this.menuActionCount] = widget3.id;
                     this.menuActionCount++;
                  }

                  if (widget3.optionType == 5 && mouseX >= localChildX && mouseY >= localChildY && mouseX < localChildX + widget3.width && mouseY < localChildY + widget3.height) {
                     this.menuActionNames[this.menuActionCount] = widget3.tooltip;
                     this.menuActionIds[this.menuActionCount] = 646;
                     this.menuParam1[this.menuActionCount] = widget3.id;
                     this.menuActionCount++;
                  }

                  if (widget3.optionType == 6 && !this.continuedDialogue && mouseX >= localChildX && mouseY >= localChildY && mouseX < localChildX + widget3.width && mouseY < localChildY + widget3.height) {
                     this.menuActionNames[this.menuActionCount] = widget3.tooltip;
                     this.menuActionIds[this.menuActionCount] = 679;
                     this.menuParam1[this.menuActionCount] = widget3.id;
                     this.menuActionCount++;
                  }

                  if (widget3.type == 2) {
                     int inventorySpriteXIndex = 0;

                     for (int loopIndex = 0; loopIndex < widget3.height; loopIndex++) {
                        for (int loopIndex2 = 0; loopIndex2 < widget3.width; loopIndex2++) {
                           int scalar = localChildX + loopIndex2 * (32 + widget3.spritePaddingX);
                           int scalar2 = localChildY + loopIndex * (32 + widget3.spritePaddingY);
                           if (inventorySpriteXIndex < 20) {
                              scalar += widget3.inventorySpriteX[inventorySpriteXIndex];
                              scalar2 += widget3.inventorySpriteY[inventorySpriteXIndex];
                           }

                           if (mouseX >= scalar && mouseY >= scalar2 && mouseX < scalar + 32 && mouseY < scalar2 + 32) {
                              this.mouseInvInterfaceIndex = inventorySpriteXIndex;
                              this.lastActiveInvInterface = widget3.id;
                              if (widget3.inventoryIds[inventorySpriteXIndex] > 0) {
                                 ItemDefinition itemDefinition = ItemDefinition.lookup(widget3.inventoryIds[inventorySpriteXIndex] - 1);
                                 if (this.itemSelected == 1 && widget3.inventoryInterface) {
                                    if (widget3.id != this.selectedItemWidgetId || inventorySpriteXIndex != this.selectedItemSlot) {
                                       this.menuActionNames[this.menuActionCount] = "Use " + this.selectedItemName + " with @lre@" + itemDefinition.name;
                                       this.menuActionIds[this.menuActionCount] = 870;
                                       this.menuParam2[this.menuActionCount] = itemDefinition.id;
                                       this.menuParam0[this.menuActionCount] = inventorySpriteXIndex;
                                       this.menuParam1[this.menuActionCount] = widget3.id;
                                       this.menuActionCount++;
                                    }
                                 } else if (this.spellSelected == 1 && widget3.inventoryInterface) {
                                    if ((this.spellUsableOn & 16) == 16) {
                                       this.menuActionNames[this.menuActionCount] = this.spellTooltip + " @lre@" + itemDefinition.name;
                                       this.menuActionIds[this.menuActionCount] = 543;
                                       this.menuParam2[this.menuActionCount] = itemDefinition.id;
                                       this.menuParam0[this.menuActionCount] = inventorySpriteXIndex;
                                       this.menuParam1[this.menuActionCount] = widget3.id;
                                       this.menuActionCount++;
                                    }
                                 } else {
                                    if (widget3.inventoryInterface) {
                                       for (int inventoryOptionIndex = 4; inventoryOptionIndex >= 3; inventoryOptionIndex--) {
                                          if (itemDefinition.inventoryOptions != null && itemDefinition.inventoryOptions[inventoryOptionIndex] != null) {
                                             this.menuActionNames[this.menuActionCount] = itemDefinition.inventoryOptions[inventoryOptionIndex] + " @lre@" + itemDefinition.name;
                                             if (inventoryOptionIndex == 3) {
                                                this.menuActionIds[this.menuActionCount] = 493;
                                             }

                                             if (inventoryOptionIndex == 4) {
                                                this.menuActionIds[this.menuActionCount] = 847;
                                             }

                                             this.menuParam2[this.menuActionCount] = itemDefinition.id;
                                             this.menuParam0[this.menuActionCount] = inventorySpriteXIndex;
                                             this.menuParam1[this.menuActionCount] = widget3.id;
                                             this.menuActionCount++;
                                          } else if (inventoryOptionIndex == 4) {
                                             this.menuActionNames[this.menuActionCount] = "Drop @lre@" + itemDefinition.name;
                                             this.menuActionIds[this.menuActionCount] = 847;
                                             this.menuParam2[this.menuActionCount] = itemDefinition.id;
                                             this.menuParam0[this.menuActionCount] = inventorySpriteXIndex;
                                             this.menuParam1[this.menuActionCount] = widget3.id;
                                             this.menuActionCount++;
                                             if (itemDefinition.id != 5509
                                                && itemDefinition.id != 5510
                                                && itemDefinition.id != 5512
                                                && itemDefinition.id != 5514
                                                && this.shiftDown) {
                                                priorityMenuActionIndex = this.menuActionCount;
                                             }
                                          }
                                       }
                                    }

                                    if (widget3.usableItems) {
                                       this.menuActionNames[this.menuActionCount] = "Use @lre@" + itemDefinition.name;
                                       this.menuActionIds[this.menuActionCount] = 447;
                                       this.menuParam2[this.menuActionCount] = itemDefinition.id;
                                       this.menuParam0[this.menuActionCount] = inventorySpriteXIndex;
                                       this.menuParam1[this.menuActionCount] = widget3.id;
                                       this.menuActionCount++;
                                    }

                                    if (widget3.inventoryInterface && itemDefinition.inventoryOptions != null) {
                                       for (int inventoryOptionIndex2 = 2; inventoryOptionIndex2 >= 0; inventoryOptionIndex2--) {
                                          if (itemDefinition.inventoryOptions[inventoryOptionIndex2] != null) {
                                             this.menuActionNames[this.menuActionCount] = itemDefinition.inventoryOptions[inventoryOptionIndex2] + " @lre@" + itemDefinition.name;
                                             if (inventoryOptionIndex2 == 0) {
                                                this.menuActionIds[this.menuActionCount] = 74;
                                             }

                                             if (inventoryOptionIndex2 == 1) {
                                                this.menuActionIds[this.menuActionCount] = 454;
                                             }

                                             if (inventoryOptionIndex2 == 2) {
                                                this.menuActionIds[this.menuActionCount] = 539;
                                             }

                                             this.menuParam2[this.menuActionCount] = itemDefinition.id;
                                             this.menuParam0[this.menuActionCount] = inventorySpriteXIndex;
                                             this.menuParam1[this.menuActionCount] = widget3.id;
                                             this.menuActionCount++;
                                             if ((itemDefinition.id == 5509 || itemDefinition.id == 5510 || itemDefinition.id == 5512 || itemDefinition.id == 5514)
                                                && itemDefinition.inventoryOptions[inventoryOptionIndex2].toLowerCase().equals("empty")
                                                && this.shiftDown) {
                                                priorityMenuActionIndex = this.menuActionCount;
                                             }
                                          }
                                       }
                                    }

                                    if (widget3.actions != null) {
                                       for (int actionIndex2 = 4; actionIndex2 >= 0; actionIndex2--) {
                                          if (widget3.actions[actionIndex2] != null) {
                                             this.menuActionNames[this.menuActionCount] = widget3.actions[actionIndex2] + " @lre@" + itemDefinition.name;
                                             if (actionIndex2 == 0) {
                                                this.menuActionIds[this.menuActionCount] = 632;
                                             }

                                             if (actionIndex2 == 1) {
                                                this.menuActionIds[this.menuActionCount] = 78;
                                             }

                                             if (actionIndex2 == 2) {
                                                this.menuActionIds[this.menuActionCount] = 867;
                                             }

                                             if (actionIndex2 == 3) {
                                                this.menuActionIds[this.menuActionCount] = 431;
                                             }

                                             if (actionIndex2 == 4) {
                                                this.menuActionIds[this.menuActionCount] = 53;
                                             }

                                             this.menuParam2[this.menuActionCount] = itemDefinition.id;
                                             this.menuParam0[this.menuActionCount] = inventorySpriteXIndex;
                                             this.menuParam1[this.menuActionCount] = widget3.id;
                                             this.menuActionCount++;
                                          }
                                       }
                                    }

                                    this.menuActionNames[this.menuActionCount] = "Examine @lre@" + itemDefinition.name;
                                    this.menuActionIds[this.menuActionCount] = 1125;
                                    this.menuParam2[this.menuActionCount] = itemDefinition.id;
                                    this.menuParam0[this.menuActionCount] = inventorySpriteXIndex;
                                    this.menuParam1[this.menuActionCount] = widget3.id;
                                    this.menuActionCount++;
                                 }
                              }
                           }

                           inventorySpriteXIndex++;
                        }
                     }
                  }
               }
            }
         }
      }
   }
   private void drawScrollBar(int height, int scalarArgument, int pixelIndex2, int newDrawX, int chatContentHeight, boolean flag) {
      if (gameframeVersion != 474) {
         this.scrollBar1.drawBackground(newDrawX, pixelIndex2);
         this.scrollBar2.drawBackground(newDrawX, pixelIndex2 + height - 16);
         Rasterizer2D.fillRectangle(height - 32, pixelIndex2 + 16, newDrawX, 2301979, 16);
         int scalar;
         if ((scalar = (height - 32) * height / chatContentHeight) < 8) {
            scalar = 8;
         }

         int scalar2 = (height - 32 - scalar) * scalarArgument / (chatContentHeight - height);
         Rasterizer2D.fillRectangle(scalar, pixelIndex2 + 16 + scalar2, newDrawX, 5063219, 16);
         Rasterizer2D.drawVerticalLine(pixelIndex2 + 16 + scalar2, 7759444, scalar, newDrawX);
         Rasterizer2D.drawVerticalLine(pixelIndex2 + 16 + scalar2, 7759444, scalar, newDrawX + 1);
         Rasterizer2D.drawHorizontalLine(pixelIndex2 + 16 + scalar2, 7759444, 16, newDrawX);
         Rasterizer2D.drawHorizontalLine(pixelIndex2 + 17 + scalar2, 7759444, 16, newDrawX);
         Rasterizer2D.drawVerticalLine(pixelIndex2 + 16 + scalar2, 3353893, scalar, newDrawX + 15);
         Rasterizer2D.drawVerticalLine(pixelIndex2 + 17 + scalar2, 3353893, scalar - 1, newDrawX + 14);
         Rasterizer2D.drawHorizontalLine(pixelIndex2 + 15 + scalar2 + scalar, 3353893, 16, newDrawX);
         Rasterizer2D.drawHorizontalLine(pixelIndex2 + 14 + scalar2 + scalar, 3353893, 15, newDrawX + 1);
      } else {
         int customSpriteIndex = 32;
         int customSpriteIndex2 = 30;
         int customSpriteIndex3 = 31;
         int customSpriteIndex4 = 29;
         byte customSpriteIndex5 = 27;
         byte customSpriteIndex6 = 28;
         if (flag) {
            customSpriteIndex = 102;
            customSpriteIndex2 = 103;
            customSpriteIndex3 = 102;
            customSpriteIndex4 = 104;
            customSpriteIndex5 = 105;
            customSpriteIndex6 = 106;
         }

         int sourceHeight = height;
         int sourceChatContentHeight = chatContentHeight;
         int loopIndex = (height - 32) / 5;
         if ((height = (height - 32) * height / chatContentHeight) < 10) {
            height = 10;
         }

         chatContentHeight = height / 5 - 2;
         int sourceLocalDrawY = (sourceHeight - 32 - height) * scalarArgument / (sourceChatContentHeight - sourceHeight) + 16 + pixelIndex2;
         int localDrawY = 0;

         for (int position = pixelIndex2 + 14; localDrawY <= loopIndex; position += 5) {
            customSprites[customSpriteIndex].drawSprite(newDrawX, position);
            localDrawY++;
         }

         customSprites[customSpriteIndex2].drawSprite(newDrawX, sourceLocalDrawY);
         localDrawY = sourceLocalDrawY;
         sourceLocalDrawY += 5;

         for (int loopIndex2 = 0; loopIndex2 <= chatContentHeight; loopIndex2++) {
            customSprites[customSpriteIndex3].drawSprite(newDrawX, sourceLocalDrawY);
            sourceLocalDrawY += 5;
         }

         sourceLocalDrawY = (sourceHeight - 32 - height) * scalarArgument / (sourceChatContentHeight - sourceHeight) + 16 + pixelIndex2 + (height - 5);
         customSprites[customSpriteIndex4].drawSprite(newDrawX, sourceLocalDrawY);
         int localDrawHeight = sourceLocalDrawY;
         if (flag) {
            int drawX = newDrawX + 1;
            int drawY = localDrawY + 1;
            int drawHeight = localDrawHeight - localDrawY + 4;
            int drawWidth = 14;
            int gradient = 0;
            int gradientStep = 65536 / drawHeight;
            if (drawX < Rasterizer2D.topX) {
               drawWidth -= Rasterizer2D.topX - drawX;
               drawX = Rasterizer2D.topX;
            }

            if (drawY < Rasterizer2D.topY) {
               gradient += (Rasterizer2D.topY - drawY) * gradientStep;
               drawHeight -= Rasterizer2D.topY - drawY;
               drawY = Rasterizer2D.topY;
            }

            if (drawX + drawWidth > Rasterizer2D.bottomX) {
               drawWidth = Rasterizer2D.bottomX - drawX;
            }

            if (drawY + drawHeight > Rasterizer2D.bottomY) {
               drawHeight = Rasterizer2D.bottomY - drawY;
            }

            int rowSkip = Rasterizer2D.width - drawWidth;
            int pixelIndex = drawX + drawY * Rasterizer2D.width;

            for (int row = -drawHeight; row < 0; row++) {
               int inverseWeight = (65536 - gradient) >> 8;
               int weight = gradient >> 8;
               int mixed;
               int rowColor = (
                     ((mixed = (inverseWeight * 11206819 + weight * 11206819 & -16711936)
                           + (inverseWeight * 43776 + weight * 43776 & 0xFF0000) >>> 8) & 16711935) << 6 >> 8 & 16711935
                  )
                  + ((mixed & 0xFF00) << 6 >> 8 & 0xFF00);

               for (int column = -drawWidth; column < 0; column++) {
                  int background = Rasterizer2D.pixels[pixelIndex];
                  background = ((background & 16711935) * 192 >> 8 & 16711935) + ((background & 0xFF00) * 192 >> 8 & 0xFF00);
                  Rasterizer2D.pixels[pixelIndex++] = rowColor + background;
               }

               pixelIndex += rowSkip;
               gradient += gradientStep;
            }
         }

         customSprites[customSpriteIndex5].drawSprite(newDrawX, pixelIndex2);
         customSprites[customSpriteIndex6].drawSprite(newDrawX, pixelIndex2 + sourceHeight - 16);
      }
   }
   private static boolean initializeMidiPlayer() {
      musicFadeTicksRemaining = 20;

      try {
         midiPlayer = (MidiPlayer)Class.forName("client.JavaMidiPlayer").newInstance();
         return true;
      } catch (Throwable throwable) {
         return false;
      }
   }
   private void updateNPCs(Buffer buffer, int pktSize) {
      this.removedEntityCount = 0;
      this.entityUpdateCount = 0;
      Buffer sourceBuffer = buffer;
      Client client = this;
      sourceBuffer.startBitAccess();
      int decodedBits;
      if ((decodedBits = sourceBuffer.readBits(8)) < client.npcCount) {
         for (int npcIndex = decodedBits; npcIndex < client.npcCount; npcIndex++) {
            client.removedEntityIndices[client.removedEntityCount++] = client.npcIndices[npcIndex];
         }
      }

      if (decodedBits > client.npcCount) {
         SignLink.reporterror(username + " Too many npcs");
         throw new RuntimeException("eek");
      }

      client.npcCount = 0;

      for (int npcIndex2 = 0; npcIndex2 < decodedBits; npcIndex2++) {
         int npcIndex3 = client.npcIndices[npcIndex2];
         Npc npc;
         (npc = client.npcs[npcIndex3]).index = npcIndex3;
         if (sourceBuffer.readBits(1) == 0) {
            client.npcIndices[client.npcCount++] = npcIndex3;
            npc.lastUpdateCycle = gameCycle;
         } else {
            int direction;
            if ((direction = sourceBuffer.readBits(2)) == 0) {
               client.npcIndices[client.npcCount++] = npcIndex3;
               npc.lastUpdateCycle = gameCycle;
               client.entityUpdateIndices[client.entityUpdateCount++] = npcIndex3;
            } else if (direction == 1) {
               client.npcIndices[client.npcCount++] = npcIndex3;
               npc.lastUpdateCycle = gameCycle;
               direction = sourceBuffer.readBits(3);
               npc.moveInDirection(false, direction);
               if (sourceBuffer.readBits(1) == 1) {
                  client.entityUpdateIndices[client.entityUpdateCount++] = npcIndex3;
               }
            } else if (direction == 2) {
               client.npcIndices[client.npcCount++] = npcIndex3;
               npc.lastUpdateCycle = gameCycle;
               direction = sourceBuffer.readBits(3);
               npc.moveInDirection(true, direction);
               direction = sourceBuffer.readBits(3);
               npc.moveInDirection(true, direction);
               if (sourceBuffer.readBits(1) == 1) {
                  client.entityUpdateIndices[client.entityUpdateCount++] = npcIndex3;
               }
            } else if (direction == 3) {
               client.removedEntityIndices[client.removedEntityCount++] = npcIndex3;
            }
         }
      }

      this.readNewNpcs(pktSize, buffer);
      this.parseNpcUpdateMasks(buffer);

      for (int removedEntityIndex = 0; removedEntityIndex < this.removedEntityCount; removedEntityIndex++) {
         int removedEntityIndex2 = this.removedEntityIndices[removedEntityIndex];
         if (this.npcs[removedEntityIndex2].lastUpdateCycle != gameCycle) {
            this.npcs[removedEntityIndex2].definition = null;
            this.npcs[removedEntityIndex2] = null;
         }
      }

      if (buffer.currentPosition != pktSize) {
         SignLink.reporterror(username + " size mismatch in getnpcpos - pos:" + buffer.currentPosition + " psize:" + pktSize);
         throw new RuntimeException("eek");
      }

      for (int npcIndex4 = 0; npcIndex4 < this.npcCount; npcIndex4++) {
         if (this.npcs[this.npcIndices[npcIndex4]] == null) {
            SignLink.reporterror(username + " null entry in npc list - pos:" + npcIndex4 + " size:" + this.npcCount);
            throw new RuntimeException("eek");
         }
      }
   }
   private void processChatModeClick() {
      if (this.fullscreenInterfaceId == -1) {
         if (super.clickButton == 1) {
            if (gameframeVersion != 474) {
               if (super.clickX >= 6 && super.clickX <= 106 && super.clickY >= 467 && super.clickY <= 499) {
                  this.publicChatMode = (this.publicChatMode + 1) % 4;
                  this.chatSettingsRedraw = true;
                  this.inputTaken = true;
                  this.outgoingBuffer.writeOpcode(95);
                  this.outgoingBuffer.writeByte(this.publicChatMode);
                  this.outgoingBuffer.writeByte(this.privateChatMode);
                  this.outgoingBuffer.writeByte(this.tradeMode);
               }

               if (super.clickX >= 135 && super.clickX <= 235 && super.clickY >= 467 && super.clickY <= 499) {
                  this.privateChatMode = (this.privateChatMode + 1) % 3;
                  this.chatSettingsRedraw = true;
                  this.inputTaken = true;
                  this.outgoingBuffer.writeOpcode(95);
                  this.outgoingBuffer.writeByte(this.publicChatMode);
                  this.outgoingBuffer.writeByte(this.privateChatMode);
                  this.outgoingBuffer.writeByte(this.tradeMode);
               }

               if (super.clickX >= 273 && super.clickX <= 373 && super.clickY >= 467 && super.clickY <= 499) {
                  this.tradeMode = (this.tradeMode + 1) % 3;
                  this.chatSettingsRedraw = true;
                  this.inputTaken = true;
                  this.outgoingBuffer.writeOpcode(95);
                  this.outgoingBuffer.writeByte(this.publicChatMode);
                  this.outgoingBuffer.writeByte(this.privateChatMode);
                  this.outgoingBuffer.writeByte(this.tradeMode);
               }

               if (super.clickX >= 412 && super.clickX <= 512 && super.clickY >= 467 && super.clickY <= 499) {
                  if (this.openInterfaceId == -1 && this.fullscreenInterfaceId == -1) {
                     this.closeTopInterfaces();
                     this.reportAbuseInput = "";
                     this.canMute = false;
                     Widget[] widgets = Widget.widgets;
                     int widgetsLengthOrWidgets = Widget.widgets.length;

                     for (int widgetIndex = 0; widgetIndex < widgetsLengthOrWidgets; widgetIndex++) {
                        Widget widget;
                        if ((widget = widgets[widgetIndex]) != null && widget.contentType == 600) {
                           this.reportAbuseInterfaceID = this.openInterfaceId = widget.parentId;
                           break;
                        }
                     }
                  } else {
                     this.pushMessage("Please close the interface you have open before using 'report abuse'", 0, "", 0, 0, 0);
                  }
               }
            } else if (super.clickX >= 404 && super.clickX <= 515 && super.clickY >= clientHeight - 23 && super.clickY <= clientHeight) {
               if (this.openInterfaceId == -1 && this.fullscreenInterfaceId == -1) {
                  this.closeTopInterfaces();
                  this.reportAbuseInput = "";
                  this.canMute = false;
                  Widget[] widgets2 = Widget.widgets;
                  int widgetsLength = Widget.widgets.length;

                  for (int widgetIndex2 = 0; widgetIndex2 < widgetsLength; widgetIndex2++) {
                     Widget widget2;
                     if ((widget2 = widgets2[widgetIndex2]) != null && widget2.contentType == 600) {
                        this.reportAbuseInterfaceID = this.openInterfaceId = widget2.parentId;
                        break;
                     }
                  }
               } else {
                  this.pushMessage("Please close the interface you have open before using 'report abuse'", 0, "", 0, 0, 0);
               }
            }

            if (++chatModeNoiseCounter > 1386) {
               chatModeNoiseCounter = 0;
               this.outgoingBuffer.writeOpcode(165);
               this.outgoingBuffer.writeByte(0);
               int currentPosition = this.outgoingBuffer.currentPosition;
               this.outgoingBuffer.writeByte(139);
               this.outgoingBuffer.writeByte(150);
               this.outgoingBuffer.writeShort(32131);
               this.outgoingBuffer.writeByte((int)(Math.random() * 256.0));
               this.outgoingBuffer.writeShort(3250);
               this.outgoingBuffer.writeByte(177);
               this.outgoingBuffer.writeShort(24859);
               this.outgoingBuffer.writeByte(119);
               if ((int)(Math.random() * 2.0) == 0) {
                  this.outgoingBuffer.writeShort(47234);
               }

               if ((int)(Math.random() * 2.0) == 0) {
                  this.outgoingBuffer.writeByte(21);
               }

               this.outgoingBuffer.writeLengthByte(this.outgoingBuffer.currentPosition - currentPosition);
            }
         }
      }
   }
   static final void mixPcmSamples(int[] values, int length) {
      int position = 0;
      length -= 7;

      while (position < length) {
         values[position++] = 0;
         values[position++] = 0;
         values[position++] = 0;
         values[position++] = 0;
         values[position++] = 0;
         values[position++] = 0;
         values[position++] = 0;
         values[position++] = 0;
      }

      length += 7;

      while (position < length) {
         values[position++] = 0;
      }

      if (pcmStream != null) {
         pcmStream.fill(values, 0, length);
      }

      advanceAudioTiming(length);
   }
   private boolean shouldIgnoreStaleCombatStyleVarp(int varpIndex, int value) {
      if (varpIndex != 43 || this.pendingCombatStyleValue == -1) {
         return false;
      }

      if (value == this.pendingCombatStyleValue) {
         this.pendingCombatStyleValue = -1;
         this.pendingCombatStyleUntilMillis = 0L;
         return false;
      }

      if (System.currentTimeMillis() <= this.pendingCombatStyleUntilMillis) {
         return true;
      }

      this.pendingCombatStyleValue = -1;
      this.pendingCombatStyleUntilMillis = 0L;
      return false;
   }
   private void applyVarpSetting(int definitionIndex) {
      int type = VarpDefinition.definitions[definitionIndex].type;
      if (VarpDefinition.definitions[definitionIndex].type != 0) {
         definitionIndex = this.varps[definitionIndex];
         if (type == 1) {
            if (definitionIndex == 1) {
               Rasterizer3D.setBrightness(0.9);
            }

            if (definitionIndex == 2) {
               Rasterizer3D.setBrightness(0.8);
            }

            if (definitionIndex == 3) {
               Rasterizer3D.setBrightness(0.7);
            }

            if (definitionIndex == 4) {
               Rasterizer3D.setBrightness(0.6);
            }

            ItemDefinition.spriteCache.clear();
            this.welcomeScreenRaised = true;
         }

         if (type == 3) {
            short sourceMusicVolumeSetting = 0;
            if (definitionIndex == 0) {
               sourceMusicVolumeSetting = 255;
            }

            if (definitionIndex == 1) {
               sourceMusicVolumeSetting = 192;
            }

            if (definitionIndex == 2) {
               sourceMusicVolumeSetting = 128;
            }

            if (definitionIndex == 3) {
               sourceMusicVolumeSetting = 64;
            }

            if (definitionIndex == 4) {
               sourceMusicVolumeSetting = 0;
            }

            if (sourceMusicVolumeSetting != musicVolumeSetting) {
               if (musicVolumeSetting == 0 && this.currentSong != -1) {
                  this.requestMusicTrackImmediate(sourceMusicVolumeSetting, this.currentSong);
                  this.previousSong = 0;
               } else if (sourceMusicVolumeSetting != 0) {
                  short localRequestedMusicVolume = sourceMusicVolumeSetting;
                  if (isMidiPlayerAvailable()) {
                     if (musicRequestPending) {
                        requestedMusicVolume = localRequestedMusicVolume;
                     } else {
                        if (midiPlayer != null) {
                           if (musicFadeTicksRemaining == 0) {
                              if (currentMusicVolume >= 0) {
                                 currentMusicVolume = localRequestedMusicVolume;
                                 midiPlayer.setVolume(localRequestedMusicVolume, 0);
                              }
                           } else if (pendingMusicData != null) {
                              pendingMusicVolume = localRequestedMusicVolume;
                           }
                        }
                     }
                  }
               } else {
                  stopMidi(false);
                  this.previousSong = 0;
               }

               musicVolumeSetting = sourceMusicVolumeSetting;

               try {
                  saveDisplaySettings();
               } catch (IOException exception) {
                  exception.printStackTrace();
               }
            }
         }

         if (type == 4) {
            if (definitionIndex == 0) {
               soundEffectVolume = 127;
            }

            if (definitionIndex == 1) {
               soundEffectVolume = 96;
            }

            if (definitionIndex == 2) {
               soundEffectVolume = 64;
            }

            if (definitionIndex == 3) {
               soundEffectVolume = 32;
            }

            if (definitionIndex == 4) {
               soundEffectVolume = 0;
            }
         }

         if (type == 5) {
            this.oneButtonMouse = definitionIndex;
         }

         if (type == 6) {
            this.chatEffectsDisabled = definitionIndex;
         }

         if (type == 8) {
            this.splitpublicChat = definitionIndex;
            this.inputTaken = true;
         }

         if (type == 9) {
            this.interfaceContent206Mode = definitionIndex;
         }
      }
   }

   static final void sleep(long longScalarArgument) {
      if (5L > 0L) {
         if (5L != 0L) {
            sleepMillis(5L);
            return;
         }

         sleepMillis(4L);
         sleepMillis(1L);
      }
   }
   private static Class loadClass(String text) {
      try {
         return Class.forName(text);
      } catch (ClassNotFoundException exception) {
         throw new NoClassDefFoundError(exception.getMessage());
      }
   }
   private void drawChangedStatsOverlay() {
      ArrayList arrayList = new ArrayList();

      for (int currentStatIndex = 0; currentStatIndex < 21; currentStatIndex++) {
         if (currentStatIndex != 3 && currentStatIndex != 5 && this.currentStats[currentStatIndex] != this.maxStats[currentStatIndex]) {
            arrayList.add(currentStatIndex);
         }
      }

      if (arrayList.size() != 0) {
         int scalar = 5 + arrayList.size() * 18;
         Rasterizer2D.darkenRectangle(7, 70, 145, scalar, 0, 50);

         for (int loopIndex = 0; loopIndex < arrayList.size(); loopIndex++) {
            int maxStatIndex = (Integer)arrayList.get(loopIndex);
            String text = this.maxStats[maxStatIndex] > this.currentStats[maxStatIndex] ? "@red@" + this.currentStats[maxStatIndex] + "@whi@" : "@gre@" + this.currentStats[maxStatIndex] + "@whi@";
            clientInstance.richBoldFont.drawBasicString(RichTextFont.capitalize(Skills.names[maxStatIndex]), 12, 85 + loopIndex * 18, 16777215, 0);
            String sourceText = text + "/" + this.maxStats[maxStatIndex];
            int scalar2 = 85 + loopIndex * 18;
            int scalar3 = 0;
            scalar3 = 16777215;
            int scalar4 = scalar2;
            short shortCode = 147;
            text = sourceText;
            RichTextFont richTextFont = clientInstance.richBoldFont;
            if (text != null) {
               richTextFont.setColorAndShadow(16777215, 0);
               richTextFont.drawBasicString(text, 147 - richTextFont.getTextWidth(text), scalar4);
            }
         }
      }
   }
   private static void drawCombatBox(Actor actor) {
      int currentHealth = actor.currentHealth;
      int maxHealth = actor.maxHealth;
      if (currentHealth != 0) {
         String text = null;
         if (actor instanceof Player) {
            text = ((Player)actor).name;
         } else if (actor instanceof Npc && ((Npc)actor).definition != null) {
            text = ((Npc)actor).definition.name;
         }

         if (text != null) {
            int localRichBoldFont = clientInstance.richBoldFont.getTextWidth(text) + 20 <= 125 ? 125 : clientInstance.richBoldFont.getTextWidth(text) + 20;
            Rasterizer2D.darkenRectangle(7, 20, localRichBoldFont, 40, 0, 50);
            if (text != null) {
               clientInstance.richBoldFont.drawCenteredString(text, 7 + localRichBoldFont / 2, 32, 16777215, 0);
            }

            Rasterizer2D.fillRectangleAlternate(9, 37, localRichBoldFont - 4, 15, 11740160);
            int scalar;
            if ((scalar = (int)((double)currentHealth / maxHealth * (localRichBoldFont - 4))) > localRichBoldFont - 4) {
               scalar = localRichBoldFont - 4;
            }

            Rasterizer2D.fillRectangleAlternate(9, 37, scalar, 15, 31744);
            clientInstance.richBoldFont.drawCenteredString(currentHealth + "/" + maxHealth, 7 + localRichBoldFont / 2, 50, 16777215, 0);
         }
      }
   }
   private void drawEntityOverlays() {
      try {
         int overheadTextHalfWidthIndex = 0;
         if ((groundItemOtherNamesEnabled || groundItemOtherLootBeam) && groundItemOtherNames.size() > 0 || (groundItemRareNamesEnabled || groundItemRareLootBeam) && groundItemRareNames.size() > 0) {
            Client client = this;
            BitmapFont bitmapFont = null;
            BitmapFont bitmapFont2 = null;
            if (groundItemOtherTextSize == 0) {
               bitmapFont = client.smallFont;
            } else if (groundItemOtherTextSize == 1) {
               bitmapFont = client.plainFont;
            } else if (groundItemOtherTextSize == 2) {
               bitmapFont = client.boldFont;
            }

            if (groundItemRareTextSize == 0) {
               bitmapFont2 = client.smallFont;
            } else if (groundItemRareTextSize == 1) {
               bitmapFont2 = client.plainFont;
            } else if (groundItemRareTextSize == 2) {
               bitmapFont2 = client.boldFont;
            }

            int position = localPlayer.pathX[0] - 16 < 0 ? 0 : localPlayer.pathX[0] - 16;
            int position2 = localPlayer.pathY[0] - 16 < 0 ? 0 : localPlayer.pathY[0] - 16;
            int deltaXOrX = localPlayer.pathX[0] + 16 + 1 > 104 ? 104 : localPlayer.pathX[0] + 16 + 1;
            int deltaYOrY = localPlayer.pathY[0] + 16 + 1 > 104 ? 104 : localPlayer.pathY[0] + 16 + 1;

            for (int loopIndex = position; loopIndex < deltaXOrX; loopIndex++) {
               for (int loopIndex2 = position2; loopIndex2 < deltaYOrY; loopIndex2++) {
                  NodeDeque nodeDeque;
                  if ((nodeDeque = client.groundItems[client.plane][loopIndex][loopIndex2]) != null) {
                     int scalar = 0;

                     for (GroundItem groundItem = (GroundItem)nodeDeque.first(); groundItem != null; groundItem = (GroundItem)nodeDeque.next()) {
                        ItemDefinition itemDefinition = ItemDefinition.lookup(groundItem.id);
                        byte byteCode = 0;
                        Iterator iterator = groundItemOtherNames.iterator();

                        while (iterator.hasNext()) {
                           String text;
                           if ((text = (String)iterator.next()) != null && text.toLowerCase().equals(itemDefinition.name.toLowerCase())) {
                              byteCode = 1;
                              break;
                           }
                        }

                        if (byteCode == 0) {
                           iterator = groundItemRareNames.iterator();

                           while (iterator.hasNext()) {
                              String localText;
                              if ((localText = (String)iterator.next()) != null && localText.toLowerCase().equals(itemDefinition.name.toLowerCase())) {
                                 byteCode = 2;
                                 break;
                              }
                           }
                        }

                        if (byteCode != 0) {
                           int scalar2 = (loopIndex << 7) + 64;
                           int worldY = (loopIndex2 << 7) + 64;
                           client.calcEntityScreenPos(scalar2, 100, worldY);
                           String text2 = itemDefinition.name;
                           if (groundItem.quantity > 1) {
                              if (groundItem.quantity >= 100000) {
                                 text2 = text2 + " (" + formatAmountShort(groundItem.quantity) + ")";
                              } else {
                                 text2 = text2 + " (" + client.numberFormat.format(groundItem.quantity) + ")";
                              }
                           }

                           if (client.spriteDrawY >= 0 && client.spriteDrawX >= 0) {
                              if (byteCode == 1) {
                                 if (groundItemOtherNamesEnabled) {
                                    bitmapFont.textCenter(0, text2, client.spriteDrawY + 5 - scalar * 15, client.spriteDrawX);
                                    bitmapFont.textCenter(groundItemOtherTextColor, text2, client.spriteDrawY + 4 - scalar * 15, client.spriteDrawX);
                                    scalar++;
                                 }

                                 if (groundItemOtherLootBeam) {
                                    ParticleDefinition particleDefinition = ParticleDefinition.definitions[0];
                                    Vector3 vector3 = new Vector3((loopIndex << 7) + 64, 5, (loopIndex2 << 7) + 64);

                                    for (int loopIndex3 = 0; loopIndex3 < particleDefinition.getSpawnCount(); loopIndex3++) {
                                       Particle particle = new Particle(particleDefinition, vector3, (loopIndex2 << 7) + 64, 7);
                                       client.addParticle(particle);
                                    }
                                 }
                              } else if (byteCode == 2) {
                                 if (groundItemRareNamesEnabled) {
                                    bitmapFont2.textCenter(0, text2, client.spriteDrawY + 5 - scalar * 15, client.spriteDrawX);
                                    bitmapFont2.textCenter(groundItemRareTextColor, text2, client.spriteDrawY + 4 - scalar * 15, client.spriteDrawX);
                                    scalar++;
                                 }

                                 if (groundItemRareLootBeam) {
                                    ParticleDefinition definition = ParticleDefinition.definitions[1];
                                    Vector3 vector32 = new Vector3((loopIndex << 7) + 64, 5, (loopIndex2 << 7) + 64);

                                    for (int loopIndex4 = 0; loopIndex4 < definition.getSpawnCount(); loopIndex4++) {
                                       Particle particle2 = new Particle(definition, vector32, (loopIndex2 << 7) + 64, 7);
                                       client.addParticle(particle2);
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         for (int playerIndex = -1; playerIndex < this.playerCount + this.npcCount; playerIndex++) {
            Actor actor;
            if (playerIndex == -1) {
               actor = localPlayer;
            } else if (playerIndex < this.playerCount) {
               actor = this.players[this.playerIndices[playerIndex]];
            } else {
               actor = this.npcs[this.npcIndices[playerIndex - this.playerCount]];
            }

            if (actor != null && actor.isVisible()) {
               if (actor instanceof Npc) {
                  NpcDefinition npcDefinition = ((Npc)actor).definition;
                  if (((Npc)actor).definition.childIds != null) {
                     npcDefinition = npcDefinition.morph();
                  }

                  if (npcDefinition == null) {
                     continue;
                  }
               }

               if (playerIndex < this.playerCount) {
                  byte byteCode2 = 30;
                  Player player;
                  if ((player = (Player)actor) == localPlayer) {
                     this.projectActorToScreen(actor, actor.height);
                     this.projectedEntityX = this.spriteDrawX;
                     this.projectedEntityY = this.spriteDrawY;
                  }

                  if (player.headIcon >= 0) {
                     this.projectActorToScreen(actor, actor.height + 15);
                     if (this.spriteDrawX >= 0) {
                        if (player.skullIcon < 2) {
                           this.skullIcons[player.skullIcon].drawSprite(this.spriteDrawX - 12, this.spriteDrawY - 30);
                           byteCode2 += 25;
                        }

                        if (player.headIcon < 7) {
                           this.headIcons[player.headIcon].drawSprite(this.spriteDrawX - 12, this.spriteDrawY - byteCode2);
                           byteCode2 += 25;
                        }
                     }
                  }

                  if (playerIndex >= 0 && this.hintIconDrawType == 10 && this.hintIconPlayerId == this.playerIndices[playerIndex]) {
                     this.projectActorToScreen(actor, actor.height + 15);
                     if (this.spriteDrawX >= 0) {
                        this.headIconsHint[0].drawSprite(this.spriteDrawX - 12, this.spriteDrawY - byteCode2);
                     }
                  }
               } else {
                  NpcDefinition definition2 = ((Npc)actor).definition;
                  byte byteCode3 = 30;
                  if (definition2.headIcon >= 0 && definition2.headIcon < this.headIcons.length) {
                     this.projectActorToScreen(actor, actor.height + 15);
                     if (this.spriteDrawX >= 0) {
                        this.headIcons[definition2.headIcon].drawSprite(this.spriteDrawX - 12, this.spriteDrawY - 30);
                        byteCode3 += 25;
                     }
                  }

                  if (this.hintIconDrawType == 1 && this.hintIconNpcId == this.npcIndices[playerIndex - this.playerCount] && gameCycle % 20 < 10) {
                     this.projectActorToScreen(actor, actor.height + 15);
                     if (this.spriteDrawX >= 0) {
                        this.headIconsHint[0].drawSprite(this.spriteDrawX - 12, this.spriteDrawY - byteCode3);
                        byteCode3 += 25;
                     }
                  }

                  if (fishIconsEnabled) {
                     for (int fishingSpotNpcIdIndex = 0; fishingSpotNpcIdIndex < 6; fishingSpotNpcIdIndex++) {
                        if (definition2.id == fishingSpotNpcIds[fishingSpotNpcIdIndex]) {
                           this.projectActorToScreen(actor, actor.height + 15);
                           if (this.spriteDrawX >= 0) {
                              ItemDefinition.getSprite(fishingSpotItemIds[fishingSpotNpcIdIndex], 1, 0).drawSprite(this.spriteDrawX - 12, this.spriteDrawY - byteCode3);
                           }
                        }
                     }
                  }
               }

               if (actor.spokenText != null
                  && (playerIndex >= this.playerCount || this.publicChatMode == 0 || this.publicChatMode == 3 || this.publicChatMode == 1 && this.isFriendOrSelf(((Player)actor).name))
                  )
                {
                  this.projectActorToScreen(actor, actor.height);
                  if (this.spriteDrawX >= 0 && overheadTextHalfWidthIndex < 50) {
                     this.overheadTextHalfWidth[overheadTextHalfWidthIndex] = this.boldFont.getRawTextWidth(actor.spokenText) / 2;
                     this.overheadTextHeight[overheadTextHalfWidthIndex] = this.boldFont.lineHeight;
                     this.overheadTextX[overheadTextHalfWidthIndex] = this.spriteDrawX;
                     this.overheadTextY[overheadTextHalfWidthIndex] = this.spriteDrawY;
                     this.textColourEffect[overheadTextHalfWidthIndex] = actor.textColor;
                     this.overheadTextEffects[overheadTextHalfWidthIndex] = actor.textEffect;
                     this.overheadTextCycles[overheadTextHalfWidthIndex] = actor.textCycle;
                     this.overheadTexts[overheadTextHalfWidthIndex++] = actor.spokenText;
                     if (this.chatEffectsDisabled == 0 && actor.textEffect > 0 && actor.textEffect <= 3) {
                        this.overheadTextHeight[overheadTextHalfWidthIndex] = this.overheadTextHeight[overheadTextHalfWidthIndex] + 10;
                        this.overheadTextY[overheadTextHalfWidthIndex] = this.overheadTextY[overheadTextHalfWidthIndex] + 5;
                     }

                     if (this.chatEffectsDisabled == 0 && actor.textEffect == 4) {
                        this.overheadTextHalfWidth[overheadTextHalfWidthIndex] = 60;
                     }

                     if (this.chatEffectsDisabled == 0 && actor.textEffect == 5) {
                        this.overheadTextHeight[overheadTextHalfWidthIndex] = this.overheadTextHeight[overheadTextHalfWidthIndex] + 5;
                     }
                  }
               }

               if (actor.healthBarEndCycle > gameCycle) {
                  try {
                     this.projectActorToScreen(actor, actor.height + 15);
                     if (this.spriteDrawX >= 0) {
                        int localCurrentHealth;
                        if ((localCurrentHealth = actor.currentHealth * 30 / actor.maxHealth) > 30) {
                           localCurrentHealth = 30;
                        }

                        if (hdHealthBar) {
                           int customSpriteIndex = actor.maxHealth >= 200 ? 109 : 107;
                           int maxHealth2 = actor.maxHealth >= 200 ? 110 : 108;
                           int canvasWidth = customSprites[customSpriteIndex].canvasWidth;
                           int currentHealth2;
                           if ((currentHealth2 = actor.currentHealth * canvasWidth / actor.maxHealth) > canvasWidth) {
                              currentHealth2 = canvasWidth;
                           }

                           customSprites[maxHealth2].drawSprite(this.spriteDrawX - canvasWidth / 2, this.spriteDrawY - 3);
                           if (currentHealth2 > 0) {
                              new Sprite(customSprites[customSpriteIndex], 0, 0, currentHealth2, 7).drawSprite(this.spriteDrawX - canvasWidth / 2, this.spriteDrawY - 3);
                           }
                        } else {
                           Rasterizer2D.fillRectangle(5, this.spriteDrawY - 3, this.spriteDrawX - 15, 65280, localCurrentHealth);
                           Rasterizer2D.fillRectangle(5, this.spriteDrawY - 3, this.spriteDrawX - 15 + localCurrentHealth, 16711680, 30 - localCurrentHealth);
                        }
                     }
                  } catch (Exception exception) {
                  }
               }

               if (combatBoxEnabled) {
                  if (actor instanceof Npc) {
                     Npc npc = (Npc)actor;
                     if (localPlayer.interactingEntity == -1) {
                        if (npc.interactingEntity - 32768 == this.localPlayerIndex) {
                           drawCombatBox(npc);
                        }
                     } else if (npc.index == localPlayer.interactingEntity) {
                        drawCombatBox(npc);
                     }
                  } else if (actor instanceof Player) {
                     Player sourceActor = (Player)actor;
                     if (localPlayer.interactingEntity == -1) {
                        if (sourceActor.interactingEntity - 32768 == this.localPlayerIndex) {
                           drawCombatBox(sourceActor);
                        }
                     } else if (sourceActor.index == localPlayer.interactingEntity - 32768) {
                        drawCombatBox(sourceActor);
                     }
                  }
               }

               for (int hitsLoopCycleIndex = 0; hitsLoopCycleIndex < 4; hitsLoopCycleIndex++) {
                  if (actor.hitsLoopCycle[hitsLoopCycleIndex] > gameCycle) {
                     this.projectActorToScreen(actor, actor.height / 2);
                     if (this.spriteDrawX >= 0) {
                        if (hitsLoopCycleIndex == 1) {
                           this.spriteDrawY -= 20;
                        }

                        if (hitsLoopCycleIndex == 2) {
                           this.spriteDrawX -= 15;
                           this.spriteDrawY -= 10;
                        }

                        if (hitsLoopCycleIndex == 3) {
                           this.spriteDrawX += 15;
                           this.spriteDrawY -= 10;
                        }

                        int hitIcon = actor.hitIcons[hitsLoopCycleIndex];
                        byte byteCode4 = 53;
                        if (hitIcon == 1) {
                           byteCode4 = 0;
                        } else if (hitIcon == 2) {
                           byteCode4 = 4;
                        } else if (hitIcon == 3) {
                           byteCode4 = 6;
                        }

                        if (hitIcon != 0 && showDamageType) {
                           customSprites[byteCode4 + 53].drawSprite(this.spriteDrawX - 30, this.spriteDrawY - 12);
                        }

                        this.hitMarks[actor.hitMarkTypes[hitsLoopCycleIndex]].drawSprite(this.spriteDrawX - 12, this.spriteDrawY - 12);
                        this.smallFont.textCenter(0, String.valueOf(actor.hitDamages[hitsLoopCycleIndex]), this.spriteDrawY + 4, this.spriteDrawX);
                        this.smallFont.textCenter(16777215, String.valueOf(actor.hitDamages[hitsLoopCycleIndex]), this.spriteDrawY + 3, this.spriteDrawX - 1);
                     }
                  }
               }
            }
         }

         for (int overheadTextXIndex = 0; overheadTextXIndex < overheadTextHalfWidthIndex; overheadTextXIndex++) {
            int overheadTextXEntry = this.overheadTextX[overheadTextXIndex];
            int overheadTextYEntry = this.overheadTextY[overheadTextXIndex];
            int overheadTextHalfWidthEntry = this.overheadTextHalfWidth[overheadTextXIndex];
            int overheadTextHeightEntry = this.overheadTextHeight[overheadTextXIndex];
            boolean flag = true;

            while (flag) {
               flag = false;

               for (int overheadTextYIndex = 0; overheadTextYIndex < overheadTextXIndex; overheadTextYIndex++) {
                  if (overheadTextYEntry + 2 > this.overheadTextY[overheadTextYIndex] - this.overheadTextHeight[overheadTextYIndex]
                     && overheadTextYEntry - overheadTextHeightEntry < this.overheadTextY[overheadTextYIndex] + 2
                     && overheadTextXEntry - overheadTextHalfWidthEntry < this.overheadTextX[overheadTextYIndex] + this.overheadTextHalfWidth[overheadTextYIndex]
                     && overheadTextXEntry + overheadTextHalfWidthEntry > this.overheadTextX[overheadTextYIndex] - this.overheadTextHalfWidth[overheadTextYIndex]
                     && this.overheadTextY[overheadTextYIndex] - this.overheadTextHeight[overheadTextYIndex] < overheadTextYEntry) {
                     overheadTextYEntry = this.overheadTextY[overheadTextYIndex] - this.overheadTextHeight[overheadTextYIndex];
                     flag = true;
                  }
               }
            }

            this.spriteDrawX = this.overheadTextX[overheadTextXIndex];
            this.spriteDrawY = this.overheadTextY[overheadTextXIndex] = overheadTextYEntry;
            String text3 = this.overheadTexts[overheadTextXIndex];
            if (this.chatEffectsDisabled == 0) {
               overheadTextYEntry = 16776960;
               if (this.textColourEffect[overheadTextXIndex] < 6) {
                  overheadTextYEntry = this.chatTextColors[this.textColourEffect[overheadTextXIndex]];
               }

               if (this.textColourEffect[overheadTextXIndex] == 6) {
                  overheadTextYEntry = this.sceneCycle % 20 >= 10 ? 16776960 : 16711680;
               }

               if (this.textColourEffect[overheadTextXIndex] == 7) {
                  overheadTextYEntry = this.sceneCycle % 20 >= 10 ? 65535 : 255;
               }

               if (this.textColourEffect[overheadTextXIndex] == 8) {
                  overheadTextYEntry = this.sceneCycle % 20 >= 10 ? 8454016 : 45056;
               }

               if (this.textColourEffect[overheadTextXIndex] == 9) {
                  if ((overheadTextHalfWidthEntry = 150 - this.overheadTextCycles[overheadTextXIndex]) < 50) {
                     overheadTextYEntry = 16711680 + overheadTextHalfWidthEntry * 1280;
                  } else if (overheadTextHalfWidthEntry < 100) {
                     overheadTextYEntry = 16776960 - 327680 * (overheadTextHalfWidthEntry - 50);
                  } else if (overheadTextHalfWidthEntry < 150) {
                     overheadTextYEntry = 65280 + 5 * (overheadTextHalfWidthEntry - 100);
                  }
               }

               if (this.textColourEffect[overheadTextXIndex] == 10) {
                  if ((overheadTextHalfWidthEntry = 150 - this.overheadTextCycles[overheadTextXIndex]) < 50) {
                     overheadTextYEntry = 16711680 + 5 * overheadTextHalfWidthEntry;
                  } else if (overheadTextHalfWidthEntry < 100) {
                     overheadTextYEntry = 16711935 - 327680 * (overheadTextHalfWidthEntry - 50);
                  } else if (overheadTextHalfWidthEntry < 150) {
                     overheadTextYEntry = 255 + 327680 * (overheadTextHalfWidthEntry - 100) - 5 * (overheadTextHalfWidthEntry - 100);
                  }
               }

               if (this.textColourEffect[overheadTextXIndex] == 11) {
                  if ((overheadTextHalfWidthEntry = 150 - this.overheadTextCycles[overheadTextXIndex]) < 50) {
                     overheadTextYEntry = 16777215 - overheadTextHalfWidthEntry * 327685;
                  } else if (overheadTextHalfWidthEntry < 100) {
                     overheadTextYEntry = 65280 + 327685 * (overheadTextHalfWidthEntry - 50);
                  } else if (overheadTextHalfWidthEntry < 150) {
                     overheadTextYEntry = 16777215 - 327680 * (overheadTextHalfWidthEntry - 100);
                  }
               }

               if (this.overheadTextEffects[overheadTextXIndex] == 0) {
                  this.boldFont.textCenter(0, text3, this.spriteDrawY + 1, this.spriteDrawX);
                  this.boldFont.textCenter(overheadTextYEntry, text3, this.spriteDrawY, this.spriteDrawX);
               }

               if (this.overheadTextEffects[overheadTextXIndex] == 1) {
                  this.boldFont.drawCenteredStringWaveY(0, text3, this.spriteDrawX, this.sceneCycle, this.spriteDrawY + 1);
                  this.boldFont.drawCenteredStringWaveY(overheadTextYEntry, text3, this.spriteDrawX, this.sceneCycle, this.spriteDrawY);
               }

               if (this.overheadTextEffects[overheadTextXIndex] == 2) {
                  this.boldFont.drawCenteredStringWaveXY(this.spriteDrawX, text3, this.sceneCycle, this.spriteDrawY + 1, 0);
                  this.boldFont.drawCenteredStringWaveXY(this.spriteDrawX, text3, this.sceneCycle, this.spriteDrawY, overheadTextYEntry);
               }

               if (this.overheadTextEffects[overheadTextXIndex] == 3) {
                  this.boldFont.drawCenteredStringWaveXYMove(150 - this.overheadTextCycles[overheadTextXIndex], text3, this.sceneCycle, this.spriteDrawY + 1, this.spriteDrawX, 0);
                  this.boldFont.drawCenteredStringWaveXYMove(150 - this.overheadTextCycles[overheadTextXIndex], text3, this.sceneCycle, this.spriteDrawY, this.spriteDrawX, overheadTextYEntry);
               }

               if (this.overheadTextEffects[overheadTextXIndex] == 4) {
                  overheadTextHalfWidthEntry = this.boldFont.getRawTextWidth(text3);
                  overheadTextHeightEntry = (150 - this.overheadTextCycles[overheadTextXIndex]) * (overheadTextHalfWidthEntry + 100) / 150;
                  Rasterizer2D.setClip(screenMode == 0 ? 334 : clientWidth, this.spriteDrawX - 50, this.spriteDrawX + 50, 0);
                  this.boldFont.textLeft(0, text3, this.spriteDrawY + 1, this.spriteDrawX + 50 - overheadTextHeightEntry);
                  this.boldFont.textLeft(overheadTextYEntry, text3, this.spriteDrawY, this.spriteDrawX + 50 - overheadTextHeightEntry);
                  Rasterizer2D.resetClip();
               }

               if (this.overheadTextEffects[overheadTextXIndex] == 5) {
                  overheadTextHalfWidthEntry = 150 - this.overheadTextCycles[overheadTextXIndex];
                  overheadTextHeightEntry = 0;
                  if (overheadTextHalfWidthEntry < 25) {
                     overheadTextHeightEntry = overheadTextHalfWidthEntry - 25;
                  } else if (overheadTextHalfWidthEntry > 125) {
                     overheadTextHeightEntry = overheadTextHalfWidthEntry - 125;
                  }

                  Rasterizer2D.setClip(this.spriteDrawY + 5, 0, screenMode == 0 ? 512 : clientHeight, this.spriteDrawY - this.boldFont.lineHeight - 1);
                  this.boldFont.textCenter(0, text3, this.spriteDrawY + 1 + overheadTextHeightEntry, this.spriteDrawX);
                  this.boldFont.textCenter(overheadTextYEntry, text3, this.spriteDrawY + overheadTextHeightEntry, this.spriteDrawX);
                  Rasterizer2D.resetClip();
               }
            } else {
               this.boldFont.textCenter(0, text3, this.spriteDrawY + 1, this.spriteDrawX);
               this.boldFont.textCenter(16776960, text3, this.spriteDrawY, this.spriteDrawX);
            }
         }
      } catch (Exception exception2) {
      }
   }
   private void removeFriend(long encodedName) {
      try {
         if (encodedName != 0L) {
            for (int friendEncodedNameIndex = 0; friendEncodedNameIndex < this.friendCount; friendEncodedNameIndex++) {
               if (this.friendEncodedNames[friendEncodedNameIndex] == encodedName) {
                  this.friendCount--;
                  this.needDrawTabArea = true;

                  for (int friendNameIndex = friendEncodedNameIndex; friendNameIndex < this.friendCount; friendNameIndex++) {
                     this.friendNames[friendNameIndex] = this.friendNames[friendNameIndex + 1];
                     this.friendWorlds[friendNameIndex] = this.friendWorlds[friendNameIndex + 1];
                     this.friendEncodedNames[friendNameIndex] = this.friendEncodedNames[friendNameIndex + 1];
                  }

                  this.outgoingBuffer.writeOpcode(215);
                  this.outgoingBuffer.writeLong(encodedName);
                  return;
               }
            }
         }
      } catch (RuntimeException exception) {
         SignLink.reporterror("18622, false, " + encodedName + ", " + exception.toString());
         throw new RuntimeException();
      }
   }
   private void drawTabArea() {
      if (screenMode == 0) {
         this.tabImageProducer.initDrawingArea();
         Rasterizer3D.scanOffsets = sidebarScanOffsets;
         if (gameframeVersion != 474) {
            this.invBack.drawBackground(0, 0);
         } else {
            customSprites[23].drawSprite(0, 0);
         }

         if (this.invOverlayInterfaceID != -1) {
            this.drawInterface(0, 0, Widget.widgets[this.invOverlayInterfaceID], 0);
         } else if (this.tabInterfaceIds[this.currentTab] != -1) {
            this.drawInterface(0, 0, Widget.widgets[this.tabInterfaceIds[this.currentTab]], 0);
         }

         if (this.menuOpen && this.menuScreenArea == 1) {
            this.drawMenu();
         }

         if (gameframeVersion != 474) {
            this.tabImageProducer.drawToBuffer(205, this.frameBuffer, 553);
         } else {
            this.tabImageProducer.drawToBuffer(205, this.frameBuffer, 547);
         }

         this.gameScreenImageProducer.initDrawingArea();
         Rasterizer3D.scanOffsets = viewportScanOffsets;
      } else {
         if (osrsResizableFrame) {
            int localClientWidth = clientWidth - customSprites[97].canvasWidth;
            int localClientHeight = clientHeight - customSprites[97].canvasHeight;
            customSprites[98].drawSpriteAlpha(localClientWidth + 24, localClientHeight + 32, 192);
            customSprites[97].drawSprite(localClientWidth, localClientHeight);
            Client client = this;
            int clientWidth2 = clientWidth - customSprites[97].canvasWidth;
            int clientHeight2 = clientHeight - customSprites[97].canvasHeight;
            int scalar = clientWidth2 - 6;
            int scalar2 = clientHeight2 - 8;
            if (client.invOverlayInterfaceID == -1 && client.tabInterfaceIds[client.currentTab] != -1) {
               if (client.currentTab == 0) {
                  customSprites[37].drawSprite(scalar + 6, scalar2 + 8);
               }

               if (client.currentTab == 1) {
                  customSprites[41].drawSprite(scalar + 44, scalar2 + 8);
               }

               if (client.currentTab == 2) {
                  customSprites[41].drawSprite(scalar + 77, scalar2 + 8);
               }

               if (client.currentTab == 3) {
                  customSprites[41].drawSprite(scalar + 110, scalar2 + 8);
               }

               if (client.currentTab == 4) {
                  customSprites[41].drawSprite(scalar + 143, scalar2 + 8);
               }

               if (client.currentTab == 5) {
                  customSprites[41].drawSprite(scalar + 176, scalar2 + 8);
               }

               if (client.currentTab == 6) {
                  customSprites[38].drawSprite(scalar + 209, scalar2 + 8);
               }
            }

            if (client.tabInterfaceIds[0] != -1 && (client.flashingSidebarId != 0 || gameCycle % 20 < 10)) {
               customSprites[1].drawSprite(scalar + 16, scalar2 + 16);
            }

            if (client.tabInterfaceIds[1] != -1 && (client.flashingSidebarId != 1 || gameCycle % 20 < 10)) {
               customSprites[2].drawSprite(scalar + 48, scalar2 + 14);
            }

            if (client.tabInterfaceIds[2] != -1 && (client.flashingSidebarId != 2 || gameCycle % 20 < 10)) {
               customSprites[3].drawSprite(scalar + 83, scalar2 + 15);
            }

            if (client.tabInterfaceIds[3] != -1 && (client.flashingSidebarId != 3 || gameCycle % 20 < 10)) {
               customSprites[42].drawSprite(scalar + 114, scalar2 + 13);
            }

            if (client.tabInterfaceIds[4] != -1 && (client.flashingSidebarId != 4 || gameCycle % 20 < 10)) {
               customSprites[5].drawSprite(scalar + 146, scalar2 + 10);
            }

            if (client.tabInterfaceIds[5] != -1 && (client.flashingSidebarId != 5 || gameCycle % 20 < 10)) {
               customSprites[6].drawSprite(scalar + 179, scalar2 + 11);
            }

            if (client.tabInterfaceIds[6] != -1 && (client.flashingSidebarId != 6 || gameCycle % 20 < 10)) {
               customSprites[7].drawSprite(scalar + 214, scalar2 + 15);
            }

            scalar = clientWidth2 - 26;
            scalar2 = clientHeight2 + 298;
            if (client.invOverlayInterfaceID == -1 && client.tabInterfaceIds[client.currentTab] != -1) {
               if (client.currentTab == 7) {
                  customSprites[39].drawSprite(scalar + 26, scalar2);
               }

               if (client.currentTab == 8) {
                  customSprites[41].drawSprite(scalar + 64, scalar2);
               }

               if (client.currentTab == 9) {
                  customSprites[41].drawSprite(scalar + 97, scalar2);
               }

               if (client.currentTab == 10) {
                  customSprites[41].drawSprite(scalar + 130, scalar2);
               }

               if (client.currentTab == 11) {
                  customSprites[41].drawSprite(scalar + 163, scalar2);
               }

               if (client.currentTab == 12) {
                  customSprites[41].drawSprite(scalar + 196, scalar2);
               }

               if (client.currentTab == 13) {
                  customSprites[40].drawSprite(scalar + 229, scalar2);
               }
            }

            if (client.tabInterfaceIds[8] != -1 && (client.flashingSidebarId != 8 || gameCycle % 20 < 10)) {
               customSprites[8].drawSprite(scalar + 69, scalar2 + 8);
            }

            if (client.tabInterfaceIds[9] != -1 && (client.flashingSidebarId != 9 || gameCycle % 20 < 10)) {
               customSprites[9].drawSprite(scalar + 102, scalar2 + 8);
            }

            if (client.tabInterfaceIds[10] != -1 && (client.flashingSidebarId != 10 || gameCycle % 20 < 10)) {
               customSprites[10].drawSprite(scalar + 136, scalar2 + 4);
            }

            if (client.tabInterfaceIds[11] != -1 && (client.flashingSidebarId != 11 || gameCycle % 20 < 10)) {
               customSprites[11].drawSprite(scalar + 168, scalar2 + 6);
            }

            if (client.tabInterfaceIds[12] != -1 && (client.flashingSidebarId != 12 || gameCycle % 20 < 10)) {
               customSprites[12].drawSprite(scalar + 204, scalar2 + 4);
            }

            if (client.tabInterfaceIds[13] != -1 && (client.flashingSidebarId != 13 || gameCycle % 20 < 10)) {
               customSprites[13].drawSprite(scalar + 236, scalar2 + 5);
            }

            if (this.invOverlayInterfaceID != -1) {
               this.drawInterface(0, localClientWidth + 26, Widget.widgets[this.invOverlayInterfaceID], localClientHeight + 37);
               return;
            }

            if (this.tabInterfaceIds[this.currentTab] != -1) {
               this.drawInterface(0, localClientWidth + 26, Widget.widgets[this.tabInterfaceIds[this.currentTab]], localClientHeight + 37);
               return;
            }
         } else {
            Client client4 = this;
            if (clientWidth >= client4.wideTabBarWidthThreshold) {
               int clientWidth3 = clientWidth - customSprites[client4.tabBarBackgroundSpriteId].canvasWidth * 14;
               int clientHeight3 = clientHeight - customSprites[client4.tabBarBackgroundSpriteId].canvasHeight;

               for (int position = 0; clientWidth3 <= clientWidth - customSprites[client4.tabBarBackgroundSpriteId].canvasWidth && position < 14; position++) {
                  customSprites[client4.tabBarBackgroundSpriteId].drawSprite(clientWidth3, clientHeight3);
                  clientWidth3 += customSprites[client4.tabBarBackgroundSpriteId].canvasWidth;
               }

               if (client4.resizableTabPanelVisible) {
                  customSprites[91].drawSpriteAlpha(clientWidth - 197, clientHeight - customSprites[client4.tabBarBackgroundSpriteId].canvasHeight - 267, 150);
                  customSprites[90].drawSprite(clientWidth - 204, clientHeight - customSprites[client4.tabBarBackgroundSpriteId].canvasHeight - 274);
               }
            } else {
               int clientWidth4 = clientWidth - customSprites[client4.tabBarBackgroundSpriteId].canvasWidth * 7;
               int clientHeight4 = clientHeight - (customSprites[client4.tabBarBackgroundSpriteId].canvasHeight << 1);

               for (int position2 = 0; clientWidth4 <= clientWidth - customSprites[client4.tabBarBackgroundSpriteId].canvasWidth && position2 < 7; position2++) {
                  customSprites[client4.tabBarBackgroundSpriteId].drawSprite(clientWidth4, clientHeight4);
                  clientWidth4 += customSprites[client4.tabBarBackgroundSpriteId].canvasWidth;
               }

               clientWidth4 = clientWidth - customSprites[client4.tabBarBackgroundSpriteId].canvasWidth * 7;
               clientHeight4 = clientHeight - customSprites[client4.tabBarBackgroundSpriteId].canvasHeight;

               for (int position3 = 0; clientWidth4 <= clientWidth - customSprites[client4.tabBarBackgroundSpriteId].canvasWidth && position3 < 7; position3++) {
                  customSprites[client4.tabBarBackgroundSpriteId].drawSprite(clientWidth4, clientHeight4);
                  clientWidth4 += customSprites[client4.tabBarBackgroundSpriteId].canvasWidth;
               }

               if (client4.resizableTabPanelVisible) {
                  customSprites[91].drawSpriteAlpha(clientWidth - 197, clientHeight - (customSprites[client4.tabBarBackgroundSpriteId].canvasHeight << 1) - 267, 150);
                  customSprites[90].drawSprite(clientWidth - 204, clientHeight - (customSprites[client4.tabBarBackgroundSpriteId].canvasHeight << 1) - 274);
               }
            }

            if (client4.invOverlayInterfaceID == -1) {
               if (client4.resizableTabPanelVisible) {
                  Client client2 = client4;
                  if (clientWidth >= client2.wideTabBarWidthThreshold) {
                     int clientWidth5 = clientWidth - customSprites[client2.tabBarBackgroundSpriteId].canvasWidth * 14;
                     int clientHeight5 = clientHeight - customSprites[client2.tabBarBackgroundSpriteId].canvasHeight;

                     for (int position4 = 0; clientWidth5 <= clientWidth - customSprites[client2.tabBarBackgroundSpriteId].canvasWidth && position4 < 14; position4++) {
                        if (client2.tabInterfaceIds[client2.currentTab] != -1 && client2.currentTab == position4) {
                           customSprites[client2.tabBarSelectedSpriteId].drawSprite(clientWidth5 - 1, clientHeight5);
                        }

                        clientWidth5 += customSprites[client2.tabBarBackgroundSpriteId].canvasWidth;
                     }
                  } else {
                     int clientWidth6 = clientWidth - customSprites[client2.tabBarBackgroundSpriteId].canvasWidth * 7;
                     int clientHeight6 = clientHeight - (customSprites[client2.tabBarBackgroundSpriteId].canvasHeight << 1);

                     for (int position5 = 0; clientWidth6 <= clientWidth - customSprites[client2.tabBarBackgroundSpriteId].canvasWidth && position5 < 7; position5++) {
                        if (client2.tabInterfaceIds[client2.currentTab] != -1 && client2.currentTab == position5) {
                           customSprites[client2.tabBarSelectedSpriteId].drawSprite(clientWidth6 - 1, clientHeight6);
                        }

                        clientWidth6 += customSprites[client2.tabBarBackgroundSpriteId].canvasWidth;
                     }

                     clientWidth6 = clientWidth - customSprites[client2.tabBarBackgroundSpriteId].canvasWidth * 7;
                     clientHeight6 = clientHeight - customSprites[client2.tabBarBackgroundSpriteId].canvasHeight;

                     for (int position6 = 7; clientWidth6 <= clientWidth - customSprites[client2.tabBarBackgroundSpriteId].canvasWidth && position6 < 14; position6++) {
                        if (client2.tabInterfaceIds[client2.currentTab] != -1 && client2.currentTab == position6) {
                           customSprites[client2.tabBarSelectedSpriteId].drawSprite(clientWidth6 - 1, clientHeight6);
                        }

                        clientWidth6 += customSprites[client2.tabBarBackgroundSpriteId].canvasWidth;
                     }
                  }
               }

               Client client3 = client4;
               int clientWidth7 = clientWidth >= client3.wideTabBarWidthThreshold
                  ? clientWidth - customSprites[client3.tabBarBackgroundSpriteId].canvasWidth * 14
                  : clientWidth - customSprites[client3.tabBarBackgroundSpriteId].canvasWidth * 7;
               int clientWidth8 = clientWidth >= client3.wideTabBarWidthThreshold
                  ? clientHeight - customSprites[client3.tabBarBackgroundSpriteId].canvasHeight
                  : clientHeight - (customSprites[client3.tabBarBackgroundSpriteId].canvasHeight << 1);
               if (client3.invOverlayInterfaceID == -1) {
                  if (client3.tabInterfaceIds[0] != -1 && (client3.flashingSidebarId != 0 || gameCycle % 20 < 10)) {
                     customSprites[1].drawSprite(clientWidth7 + 7, clientWidth8 + 8);
                  }

                  if (client3.tabInterfaceIds[1] != -1 && (client3.flashingSidebarId != 1 || gameCycle % 20 < 10)) {
                     customSprites[2].drawSprite(clientWidth7 + 37, clientWidth8 + 6);
                  }

                  if (client3.tabInterfaceIds[2] != -1 && (client3.flashingSidebarId != 2 || gameCycle % 20 < 10)) {
                     customSprites[3].drawSprite(clientWidth7 + 72, clientWidth8 + 7);
                  }

                  if (client3.tabInterfaceIds[3] != -1 && (client3.flashingSidebarId != 3 || gameCycle % 20 < 10)) {
                     customSprites[42].drawSprite(clientWidth7 + 103, clientWidth8 + 5);
                  }

                  if (client3.tabInterfaceIds[4] != -1 && (client3.flashingSidebarId != 4 || gameCycle % 20 < 10)) {
                     customSprites[5].drawSprite(clientWidth7 + 135, clientWidth8 + 2);
                  }

                  if (client3.tabInterfaceIds[5] != -1 && (client3.flashingSidebarId != 5 || gameCycle % 20 < 10)) {
                     customSprites[6].drawSprite(clientWidth7 + 168, clientWidth8 + 3);
                  }

                  if (client3.tabInterfaceIds[6] != -1 && (client3.flashingSidebarId != 6 || gameCycle % 20 < 10)) {
                     customSprites[7].drawSprite(clientWidth7 + 202, clientWidth8 + 7);
                  }

                  if (clientWidth < client3.wideTabBarWidthThreshold) {
                     clientWidth8 += customSprites[client3.tabBarBackgroundSpriteId].canvasHeight;
                  }

                  if (clientWidth >= client3.wideTabBarWidthThreshold) {
                     clientWidth7 += customSprites[client3.tabBarBackgroundSpriteId].canvasWidth * 7;
                  }

                  if (client3.tabInterfaceIds[8] != -1 && (client3.flashingSidebarId != 8 || gameCycle % 20 < 10)) {
                     customSprites[8].drawSprite(clientWidth7 + 38, clientWidth8 + 8);
                  }

                  if (client3.tabInterfaceIds[9] != -1 && (client3.flashingSidebarId != 9 || gameCycle % 20 < 10)) {
                     customSprites[9].drawSprite(clientWidth7 + 71, clientWidth8 + 8);
                  }

                  if (client3.tabInterfaceIds[10] != -1 && (client3.flashingSidebarId != 10 || gameCycle % 20 < 10)) {
                     customSprites[10].drawSprite(clientWidth7 + 105, clientWidth8 + 4);
                  }

                  if (client3.tabInterfaceIds[11] != -1 && (client3.flashingSidebarId != 11 || gameCycle % 20 < 10)) {
                     customSprites[11].drawSprite(clientWidth7 + 137, clientWidth8 + 6);
                  }

                  if (client3.tabInterfaceIds[12] != -1 && (client3.flashingSidebarId != 12 || gameCycle % 20 < 10)) {
                     customSprites[12].drawSprite(clientWidth7 + 173, clientWidth8 + 4);
                  }

                  if (client3.tabInterfaceIds[13] != -1 && (client3.flashingSidebarId != 13 || gameCycle % 20 < 10)) {
                     customSprites[13].drawSprite(clientWidth7 + 204, clientWidth8 + 5);
                  }
               }
            }

            if (client4.openInterfaceId == 5292) {
               client4.resizableTabPanelVisible = true;
            }

            int clientWidth9 = clientWidth >= client4.wideTabBarWidthThreshold ? 37 : 74;
            if (client4.resizableTabPanelVisible) {
               if (client4.invOverlayInterfaceID != -1) {
                  client4.drawInterface(
                     0, screenMode == 0 ? 28 : clientWidth - 197, Widget.widgets[client4.invOverlayInterfaceID], screenMode == 0 ? 37 : clientHeight - clientWidth9 - 267
                  );
               } else if (client4.tabInterfaceIds[client4.currentTab] != -1) {
                  client4.drawInterface(
                     0,
                     screenMode == 0 ? 28 : clientWidth - 197,
                     Widget.widgets[client4.tabInterfaceIds[client4.currentTab]],
                     screenMode == 0 ? 37 : clientHeight - clientWidth9 - 267
                  );
               }

               if (client4.menuOpen && client4.menuScreenArea == 1) {
                  client4.drawMenu();
               }
            }
         }
      }
   }
   private void animateTextures(int textureUsageCounter) {
      if (Rasterizer3D.textureLastUsed[17] >= textureUsageCounter) {
         IndexedSprite indexedSprite;
         int loopIndex = (indexedSprite = Rasterizer3D.textures[17]).width * indexedSprite.height - 1;
         int localWidth = indexedSprite.width * this.animationCycleDelta << 1;
         byte[] animatedTextureScratchOrPixelIndices = indexedSprite.pixelIndices;
         byte[] pixelIndicesOrAnimatedTextureScratch = this.animatedTextureScratch;

         for (int loopIndex2 = 0; loopIndex2 <= loopIndex; loopIndex2++) {
            pixelIndicesOrAnimatedTextureScratch[loopIndex2] = animatedTextureScratchOrPixelIndices[loopIndex2 - localWidth & loopIndex];
         }

         indexedSprite.pixelIndices = pixelIndicesOrAnimatedTextureScratch;
         this.animatedTextureScratch = animatedTextureScratchOrPixelIndices;
         Rasterizer3D.releaseTexture(17);
         if (++textureAnimationNoiseCounter > 1235) {
            textureAnimationNoiseCounter = 0;
            this.outgoingBuffer.writeOpcode(226);
            this.outgoingBuffer.writeByte(0);
            int currentPosition = this.outgoingBuffer.currentPosition;
            this.outgoingBuffer.writeShort(58722);
            this.outgoingBuffer.writeByte(240);
            this.outgoingBuffer.writeShort((int)(Math.random() * 65536.0));
            this.outgoingBuffer.writeByte((int)(Math.random() * 256.0));
            if ((int)(Math.random() * 2.0) == 0) {
               this.outgoingBuffer.writeShort(51825);
            }

            this.outgoingBuffer.writeByte((int)(Math.random() * 256.0));
            this.outgoingBuffer.writeShort((int)(Math.random() * 65536.0));
            this.outgoingBuffer.writeShort(7130);
            this.outgoingBuffer.writeShort((int)(Math.random() * 65536.0));
            this.outgoingBuffer.writeShort(61657);
            this.outgoingBuffer.writeLengthByte(this.outgoingBuffer.currentPosition - currentPosition);
         }
      }

      if (Rasterizer3D.textureLastUsed[24] >= textureUsageCounter) {
         IndexedSprite indexedSprite4;
         int loopIndex3 = (indexedSprite4 = Rasterizer3D.textures[24]).width * indexedSprite4.height - 1;
         int width2 = indexedSprite4.width * this.animationCycleDelta << 1;
         byte[] pixelIndices = indexedSprite4.pixelIndices;
         byte[] animatedTextureScratch = this.animatedTextureScratch;

         for (int animatedTextureScratchIndex = 0; animatedTextureScratchIndex <= loopIndex3; animatedTextureScratchIndex++) {
            animatedTextureScratch[animatedTextureScratchIndex] = pixelIndices[animatedTextureScratchIndex - width2 & loopIndex3];
         }

         indexedSprite4.pixelIndices = animatedTextureScratch;
         this.animatedTextureScratch = pixelIndices;
         Rasterizer3D.releaseTexture(24);
      }

      if (Rasterizer3D.textureLastUsed[34] >= textureUsageCounter) {
         IndexedSprite indexedSprite2;
         int loopIndex4 = (indexedSprite2 = Rasterizer3D.textures[34]).width * indexedSprite2.height - 1;
         int width3 = indexedSprite2.width * this.animationCycleDelta << 1;
         byte[] animatedTextureScratchOrPixelIndices2 = indexedSprite2.pixelIndices;
         byte[] pixelIndicesOrAnimatedTextureScratch2 = this.animatedTextureScratch;

         for (int loopIndex5 = 0; loopIndex5 <= loopIndex4; loopIndex5++) {
            pixelIndicesOrAnimatedTextureScratch2[loopIndex5] = animatedTextureScratchOrPixelIndices2[loopIndex5 - width3 & loopIndex4];
         }

         indexedSprite2.pixelIndices = pixelIndicesOrAnimatedTextureScratch2;
         this.animatedTextureScratch = animatedTextureScratchOrPixelIndices2;
         Rasterizer3D.releaseTexture(34);
      }

      if (Rasterizer3D.textureLastUsed[40] >= textureUsageCounter) {
         IndexedSprite indexedSprite3;
         int loopIndex6 = (indexedSprite3 = Rasterizer3D.textures[40]).width * indexedSprite3.height - 1;
         int width4 = indexedSprite3.width * this.animationCycleDelta << 1;
         byte[] animatedTextureScratchOrPixelIndices3 = indexedSprite3.pixelIndices;
         byte[] pixelIndicesOrAnimatedTextureScratch3 = this.animatedTextureScratch;

         for (int loopIndex7 = 0; loopIndex7 <= loopIndex6; loopIndex7++) {
            pixelIndicesOrAnimatedTextureScratch3[loopIndex7] = animatedTextureScratchOrPixelIndices3[loopIndex7 - width4 & loopIndex6];
         }

         indexedSprite3.pixelIndices = pixelIndicesOrAnimatedTextureScratch3;
         this.animatedTextureScratch = animatedTextureScratchOrPixelIndices3;
         Rasterizer3D.releaseTexture(40);
      }
   }
   private void updateSpokenTextCycles() {
      for (int playerIndex2 = -1; playerIndex2 < this.playerCount; playerIndex2++) {
         int playerIndex;
         if (playerIndex2 == -1) {
            playerIndex = 2047;
         } else {
            playerIndex = this.playerIndices[playerIndex2];
         }

         Player player;
         if ((player = this.players[playerIndex]) != null && player.textCycle > 0) {
            player.textCycle--;
            if (player.textCycle == 0) {
               player.spokenText = null;
            }
         }
      }

      for (int npcIndex = 0; npcIndex < this.npcCount; npcIndex++) {
         int npcIndex2 = this.npcIndices[npcIndex];
         Npc npc;
         if ((npc = this.npcs[npcIndex2]) != null && npc.textCycle > 0) {
            npc.textCycle--;
            if (npc.textCycle == 0) {
               npc.spokenText = null;
            }
         }
      }
   }
   private void calculateCameraPosition() {
      int sourceCameraPositionX = (this.x << 7) + 64;
      int sourceXCameraPos = (this.y << 7) + 64;
      int cameraPositionZOrGetTileHeight = this.getTileHeight(this.plane, sourceXCameraPos, sourceCameraPositionX) - this.height;
      if (this.cameraPositionX < sourceCameraPositionX) {
         this.cameraPositionX = this.cameraPositionX + this.speed + (sourceCameraPositionX - this.cameraPositionX) * this.angle / 1000;
         if (this.cameraPositionX > sourceCameraPositionX) {
            this.cameraPositionX = sourceCameraPositionX;
         }
      }

      if (this.cameraPositionX > sourceCameraPositionX) {
         this.cameraPositionX = this.cameraPositionX - (this.speed + (this.cameraPositionX - sourceCameraPositionX) * this.angle / 1000);
         if (this.cameraPositionX < sourceCameraPositionX) {
            this.cameraPositionX = sourceCameraPositionX;
         }
      }

      if (this.cameraPositionZ < cameraPositionZOrGetTileHeight) {
         this.cameraPositionZ = this.cameraPositionZ + this.speed + (cameraPositionZOrGetTileHeight - this.cameraPositionZ) * this.angle / 1000;
         if (this.cameraPositionZ > cameraPositionZOrGetTileHeight) {
            this.cameraPositionZ = cameraPositionZOrGetTileHeight;
         }
      }

      if (this.cameraPositionZ > cameraPositionZOrGetTileHeight) {
         this.cameraPositionZ = this.cameraPositionZ - (this.speed + (this.cameraPositionZ - cameraPositionZOrGetTileHeight) * this.angle / 1000);
         if (this.cameraPositionZ < cameraPositionZOrGetTileHeight) {
            this.cameraPositionZ = cameraPositionZOrGetTileHeight;
         }
      }

      if (this.xCameraPos < sourceXCameraPos) {
         this.xCameraPos = this.xCameraPos + this.speed + (sourceXCameraPos - this.xCameraPos) * this.angle / 1000;
         if (this.xCameraPos > sourceXCameraPos) {
            this.xCameraPos = sourceXCameraPos;
         }
      }

      if (this.xCameraPos > sourceXCameraPos) {
         this.xCameraPos = this.xCameraPos - (this.speed + (this.xCameraPos - sourceXCameraPos) * this.angle / 1000);
         if (this.xCameraPos < sourceXCameraPos) {
            this.xCameraPos = sourceXCameraPos;
         }
      }

      sourceCameraPositionX = (this.cameraTargetTileX << 7) + 64;
      sourceXCameraPos = (this.cameraTargetTileY << 7) + 64;
      cameraPositionZOrGetTileHeight = this.getTileHeight(this.plane, sourceXCameraPos, sourceCameraPositionX) - this.cameraTargetHeightOffset;
      sourceCameraPositionX -= this.cameraPositionX;
      cameraPositionZOrGetTileHeight -= this.cameraPositionZ;
      sourceXCameraPos -= this.xCameraPos;
      int sqrtResult = (int)Math.sqrt(sourceCameraPositionX * sourceCameraPositionX + sourceXCameraPos * sourceXCameraPos);
      cameraPositionZOrGetTileHeight = (int)(Math.atan2(cameraPositionZOrGetTileHeight, sqrtResult) * 325.949) & 2047;
      sourceCameraPositionX = (int)(Math.atan2(sourceCameraPositionX, sourceXCameraPos) * -325.949) & 2047;
      if (cameraPositionZOrGetTileHeight < 128) {
         cameraPositionZOrGetTileHeight = 128;
      }

      if (cameraPositionZOrGetTileHeight > 383) {
         cameraPositionZOrGetTileHeight = 383;
      }

      if (this.zCameraPos < cameraPositionZOrGetTileHeight) {
         this.zCameraPos = this.zCameraPos + this.cameraTargetMoveSpeed + (cameraPositionZOrGetTileHeight - this.zCameraPos) * this.cameraTargetMoveAcceleration / 1000;
         if (this.zCameraPos > cameraPositionZOrGetTileHeight) {
            this.zCameraPos = cameraPositionZOrGetTileHeight;
         }
      }

      if (this.zCameraPos > cameraPositionZOrGetTileHeight) {
         this.zCameraPos = this.zCameraPos - (this.cameraTargetMoveSpeed + (this.zCameraPos - cameraPositionZOrGetTileHeight) * this.cameraTargetMoveAcceleration / 1000);
         if (this.zCameraPos < cameraPositionZOrGetTileHeight) {
            this.zCameraPos = cameraPositionZOrGetTileHeight;
         }
      }

      if ((sourceXCameraPos = sourceCameraPositionX - this.yCameraPos) > 1024) {
         sourceXCameraPos -= 2048;
      }

      if (sourceXCameraPos < -1024) {
         sourceXCameraPos += 2048;
      }

      if (sourceXCameraPos > 0) {
         this.yCameraPos = this.yCameraPos + this.cameraTargetMoveSpeed + sourceXCameraPos * this.cameraTargetMoveAcceleration / 1000;
         this.yCameraPos &= 2047;
      }

      if (sourceXCameraPos < 0) {
         this.yCameraPos = this.yCameraPos - (this.cameraTargetMoveSpeed + -sourceXCameraPos * this.cameraTargetMoveAcceleration / 1000);
         this.yCameraPos &= 2047;
      }

      if ((cameraPositionZOrGetTileHeight = sourceCameraPositionX - this.yCameraPos) > 1024) {
         cameraPositionZOrGetTileHeight -= 2048;
      }

      if (cameraPositionZOrGetTileHeight < -1024) {
         cameraPositionZOrGetTileHeight += 2048;
      }

      if (cameraPositionZOrGetTileHeight < 0 && sourceXCameraPos > 0 || cameraPositionZOrGetTileHeight > 0 && sourceXCameraPos < 0) {
         this.yCameraPos = sourceCameraPositionX;
      }
   }
   private void drawMenu() {
      int menuOffsetX = this.menuOffsetX;
      int menuOffsetY = this.menuOffsetY;
      int menuWidth = this.menuWidth;
      int menuHeight = this.menuHeight;
      Rasterizer2D.fillRectangle(this.menuHeight, menuOffsetY, menuOffsetX, 6116423, menuWidth);
      Rasterizer2D.fillRectangle(16, menuOffsetY + 1, menuOffsetX + 1, 0, menuWidth - 2);
      Rasterizer2D.drawRectangle(menuOffsetX + 1, menuWidth - 2, menuHeight - 19, 0, menuOffsetY + 18);
      this.boldFont.textLeft(6116423, "Choose Option", menuOffsetY + 14, menuOffsetX + 3);
      menuHeight = this.getMenuMouseX();
      int mouseY = this.getMenuMouseY();
      if (this.menuScreenArea == 0) {
         menuHeight -= screenMode == 0 ? 4 : 0;
         mouseY -= screenMode == 0 ? 4 : 0;
      }

      if (this.menuScreenArea == 1) {
         menuHeight -= 553;
         mouseY -= 205;
      }

      if (this.menuScreenArea == 2) {
         if (gameframeVersion != 474) {
            menuHeight -= 17;
            mouseY -= 357;
         } else {
            menuHeight -= 7;
            mouseY -= 345;
         }
      }

      for (int sourceHoveredMenuActionIndex = 0; sourceHoveredMenuActionIndex < this.menuActionCount; sourceHoveredMenuActionIndex++) {
         int scalar = menuOffsetY + 31 + (this.menuActionCount - 1 - sourceHoveredMenuActionIndex) * 15;
         int textColor = 16777215;
         if (menuHeight > menuOffsetX && menuHeight < menuOffsetX + menuWidth && mouseY > scalar - 13 && mouseY < scalar + 3) {
            textColor = 16776960;
            this.hoveredMenuActionIndex = sourceHoveredMenuActionIndex;
         }

         this.richBoldFont.drawBasicString(this.menuActionNames[sourceHoveredMenuActionIndex], menuOffsetX + 3, scalar, textColor, 0);
      }
   }
   private void addFriend(long friendEncodedName) {
      try {
         if (friendEncodedName != 0L) {
            if (this.friendCount >= 100 && this.member != 1) {
               this.pushMessage("Your friendlist is full. Max of 100 for free users, and 200 for members", 0, "", 0, 0, 0);
               return;
            }

            if (this.friendCount >= 200) {
               this.pushMessage("Your friendlist is full. Max of 100 for free users, and 200 for members", 0, "", 0, 0, 0);
               return;
            }

            String text = NameUtils.formatDisplayName(NameUtils.decodeBase37(friendEncodedName));

            for (int friendEncodedNameIndex = 0; friendEncodedNameIndex < this.friendCount; friendEncodedNameIndex++) {
               if (this.friendEncodedNames[friendEncodedNameIndex] == friendEncodedName) {
                  this.pushMessage(text + " is already on your friend list", 0, "", 0, 0, 0);
                  return;
               }
            }

            for (int ignoreListAsLongIndex = 0; ignoreListAsLongIndex < this.ignoreCount; ignoreListAsLongIndex++) {
               if (this.ignoreListAsLongs[ignoreListAsLongIndex] == friendEncodedName) {
                  this.pushMessage("Please remove " + text + " from your ignore list first", 0, "", 0, 0, 0);
                  return;
               }
            }

            if (!text.equals(localPlayer.name)) {
               this.friendNames[this.friendCount] = text;
               this.friendEncodedNames[this.friendCount] = friendEncodedName;
               this.friendWorlds[this.friendCount] = 0;
               this.friendCount++;
               this.needDrawTabArea = true;
               this.outgoingBuffer.writeOpcode(188);
               this.outgoingBuffer.writeLong(friendEncodedName);
               return;
            }
         }
      } catch (RuntimeException exception) {
         SignLink.reporterror("15283, 68, " + friendEncodedName + ", " + exception.toString());
         throw new RuntimeException();
      }
   }
   private int getTileHeight(int plane, int worldY, int newIntGroundArray) {
      int position = newIntGroundArray >> 7;
      int position2 = worldY >> 7;
      if (position >= 0 && position2 >= 0 && position <= 103 && position2 <= 103) {
         int intGroundArrayIndex = plane;
         if (plane < 3 && (this.byteGroundArray[1][position][position2] & 2) == 2) {
            intGroundArrayIndex = plane + 1;
         }

         plane = newIntGroundArray & 127;
         worldY &= 127;
         newIntGroundArray = this.intGroundArray[intGroundArrayIndex][position][position2] * (128 - plane) + this.intGroundArray[intGroundArrayIndex][position + 1][position2] * plane >> 7;
         plane = this.intGroundArray[intGroundArrayIndex][position][position2 + 1] * (128 - plane) + this.intGroundArray[intGroundArrayIndex][position + 1][position2 + 1] * plane >> 7;
         return newIntGroundArray * (128 - worldY) + plane * worldY >> 7;
      } else {
         return 0;
      }
   }
   private static String formatAmountShort(int inventoryAmount) {
      if (inventoryAmount < 100000) {
         return String.valueOf(inventoryAmount);
      } else {
         return inventoryAmount < 10000000 ? inventoryAmount / 1000 + "K" : inventoryAmount / 1000000 + "M";
      }
   }
   private static boolean isMidiPlayerAvailable() {
      return midiPlayer != null;
   }
   private void resetLogout() {
      try {
         if (this.connection != null) {
            this.connection.close();
         }
      } catch (Exception exception) {
      }

      this.connection = null;
      loggedIn = false;
      Client client = this;
      this.serverVarps = new int[2000];

      for (int varpIndex = 0; varpIndex < client.varps.length; varpIndex++) {
         if (varpIndex != 168 && client.varps[varpIndex] != client.serverVarps[varpIndex]) {
            client.varps[varpIndex] = client.serverVarps[varpIndex];
            client.applyVarpSetting(varpIndex);
            client.needDrawTabArea = true;
         }
      }

      this.loginScreenState = 0;
      clearModelCaches();
      this.scene.initToNull();

      for (int collisionMapIndex = 0; collisionMapIndex < 4; collisionMapIndex++) {
         this.collisionMaps[collisionMapIndex].reset();
      }

      System.gc();
      signalMidiStop();
      this.currentSong = -1;
      this.nextSong = -1;
      this.previousSong = 0;
      this.customSettingVisualFixes = false;
      ExperienceDrop.drops.clear();
      this.updateSeasonalTheme();
      this.requestMusicTrackWithFade(18, musicVolumeSetting, this.customSettingShowExperiencePerHourStartLevels);
   }
   private void resetCharacterDesign() {
      this.characterDesignNeedsRebuild = true;

      for (int characterDesignKitIdIndex = 0; characterDesignKitIdIndex < 7; characterDesignKitIdIndex++) {
         this.characterDesignKitIds[characterDesignKitIdIndex] = -1;

         for (int kitIndex = 0; kitIndex < IdentityKit.length; kitIndex++) {
            if (!IdentityKit.kits[kitIndex].nonSelectable && IdentityKit.kits[kitIndex].bodyPartId == characterDesignKitIdIndex + (this.maleCharacter ? 0 : 7)) {
               this.characterDesignKitIds[characterDesignKitIdIndex] = kitIndex;
               break;
            }
         }
      }
   }
   private void readNewNpcs(int pktSize, Buffer buffer) {
      int npcIndex;
      while (buffer.bitPosition + 21 < pktSize << 3 && (npcIndex = buffer.readBits(14)) != 16383) {
         if (this.npcs[npcIndex] == null) {
            this.npcs[npcIndex] = new Npc();
         }

         Npc npc = this.npcs[npcIndex];
         this.npcIndices[this.npcCount++] = npcIndex;
         npc.lastUpdateCycle = gameCycle;
         int decodedBits;
         if ((decodedBits = buffer.readBits(5)) > 15) {
            decodedBits -= 32;
         }

         int readBits2;
         if ((readBits2 = buffer.readBits(5)) > 15) {
            readBits2 -= 32;
         }

         int readBits3 = buffer.readBits(1);
         npc.definition = NpcDefinition.lookup(buffer.readBits(npcIndexBitCount));
         if (buffer.readBits(1) == 1) {
            this.entityUpdateIndices[this.entityUpdateCount++] = npcIndex;
         }

         npc.size = npc.definition.size;
         npc.turnSpeed = npc.definition.turnSpeed;
         npc.walkAnimationId = npc.definition.walkAnimationId;
         npc.turnAroundAnimationId = npc.definition.turnAroundAnimationId;
         npc.turnRightAnimationId = npc.definition.turnRightAnimationId;
         npc.turnLeftAnimationId = npc.definition.turnLeftAnimationId;
         npc.idleAnimationId = npc.definition.idleAnimationId;
         npc.setPosition(localPlayer.pathX[0] + readBits2, localPlayer.pathY[0] + decodedBits, readBits3 == 1);
      }

      buffer.finishBitAccess();
   }
   @Override
   public final void processGameLoop() {
      if (!this.loadingError) {
         gameCycle++;
         if (isMidiPlayerAvailable()) {
            if (musicRequestPending) {
               byte[] sourceLoadedMusicData = loadedMusicData;
               if (loadedMusicData != null) {
                  if (musicFadeDuration >= 0) {
                     byte[] sourcePendingMusicData = sourceLoadedMusicData;
                     int pendingMusicVolumeOrRequestedMusicVolume = requestedMusicVolume;
                     int musicFadeStepOrMusicFadeDuration = musicFadeDuration;
                     boolean pendingMusicLoopOrRequestedMusicLoop = requestedMusicLoop;
                     if (midiPlayer != null) {
                        if (currentMusicVolume < 0) {
                           if (musicFadeTicksRemaining == 0) {
                              playMidiTrack(pendingMusicVolumeOrRequestedMusicVolume, sourcePendingMusicData, pendingMusicLoopOrRequestedMusicLoop);
                           } else {
                              pendingMusicVolume = pendingMusicVolumeOrRequestedMusicVolume;
                              pendingMusicLoop = pendingMusicLoopOrRequestedMusicLoop;
                              pendingMusicData = sourcePendingMusicData;
                           }
                        } else {
                           musicFadeStep = musicFadeStepOrMusicFadeDuration;
                           if (currentMusicVolume == 0 || (musicFadeTicksRemaining = (convertMidiVolumeToAttenuation(currentMusicVolume) - musicFadePosition + 3600) / musicFadeStepOrMusicFadeDuration) <= 0) {
                              musicFadeTicksRemaining = 1;
                           }

                           pendingMusicData = sourcePendingMusicData;
                           pendingMusicVolume = pendingMusicVolumeOrRequestedMusicVolume;
                           pendingMusicLoop = pendingMusicLoopOrRequestedMusicLoop;
                        }
                     }
                  } else if (musicTransitionDelay >= 0) {
                     int sourceRequestedMusicVolume = requestedMusicVolume;
                     byte[] pendingMusicData2 = sourceLoadedMusicData;
                     boolean sourceRequestedMusicLoop = requestedMusicLoop;
                     byte byteCode = -1;
                     int musicFadeTicksRemainingOrMusicTransitionDelay = musicTransitionDelay;
                     if (midiPlayer != null) {
                        if (-1 >= ~currentMusicVolume) {
                           musicFadeTicksRemainingOrMusicTransitionDelay -= 20;
                           if (musicFadeTicksRemainingOrMusicTransitionDelay <= 0) {
                              musicFadeTicksRemainingOrMusicTransitionDelay = 1;
                           }

                           musicFadeTicksRemaining = musicFadeTicksRemainingOrMusicTransitionDelay;
                           if (currentMusicVolume == 0) {
                              musicFadeStep = 0;
                           } else {
                              musicFadeTicksRemainingOrMusicTransitionDelay = convertMidiVolumeToAttenuation(currentMusicVolume) - musicFadePosition;
                              musicFadeStep = (musicFadeStep - 1 + musicFadeTicksRemainingOrMusicTransitionDelay + 3600) / musicFadeStep;
                           }

                           pendingMusicLoop = sourceRequestedMusicLoop;
                           pendingMusicData = pendingMusicData2;
                           pendingMusicVolume = sourceRequestedMusicVolume;
                        } else if (musicFadeTicksRemaining != 0) {
                           pendingMusicLoop = sourceRequestedMusicLoop;
                           pendingMusicData = pendingMusicData2;
                           pendingMusicVolume = sourceRequestedMusicVolume;
                        } else {
                           playMidiTrack(sourceRequestedMusicVolume, pendingMusicData2, sourceRequestedMusicLoop);
                        }
                     }
                  } else {
                     playMidiTrack(requestedMusicVolume, sourceLoadedMusicData, requestedMusicLoop);
                  }

                  musicRequestPending = false;
               }
            }

            boolean flag = false;
            if (midiPlayer != null) {
               if (currentMusicVolume < 0) {
                  if (musicFadeTicksRemaining > 0 && --musicFadeTicksRemaining == 0) {
                     if (pendingMusicData == null) {
                        midiPlayer.resetVolume(256);
                     } else {
                        midiPlayer.resetVolume(pendingMusicVolume);
                        currentMusicVolume = pendingMusicVolume;
                        midiPlayer.play(pendingMusicVolume, pendingMusicData, pendingMusicLoop);
                        pendingMusicData = null;
                     }

                     musicFadePosition = 0;
                  }
               } else if (musicFadeTicksRemaining > 0) {
                  musicFadePosition = musicFadePosition + musicFadeStep;
                  midiPlayer.setVolume(currentMusicVolume, musicFadePosition);
                  if (--musicFadeTicksRemaining == 0) {
                     midiPlayer.stop();
                     musicFadeTicksRemaining = 20;
                     currentMusicVolume = -1;
                  }
               }

               midiPlayer.stopIfRequested(-122);
            }
         }

         long lastAudioUpdateMillisOrCurrentTimeMillis;
         if (audioPlayer != null && (lastAudioUpdateMillisOrCurrentTimeMillis = System.currentTimeMillis()) > lastAudioUpdateMillis) {
            audioPlayer.process(lastAudioUpdateMillisOrCurrentTimeMillis);
            int scalar = (int)(-lastAudioUpdateMillis + lastAudioUpdateMillisOrCurrentTimeMillis);
            lastAudioUpdateMillis = lastAudioUpdateMillisOrCurrentTimeMillis;
            synchronized (audioPlayerClass != null ? audioPlayerClass : (audioPlayerClass = loadClass("client.AudioPlayerBase"))) {
               int sampleCount;
               if ((sampleCount = ((pcmBacklogMicros = pcmBacklogMicros + audioSampleRate * scalar) - audioSampleRate * 2000) / 1000) > 0) {
                  if (pcmStream != null) {
                     pcmStream.skip(sampleCount);
                  }

                  pcmBacklogMicros -= sampleCount * 1000;
               }
            }
         }

         if (loggedIn) {
            this.mainGameProcessor();
         } else {
            Client client = this;
            if (super.clickButton == 1 && client.clickX >= 725 && client.clickX <= 760 && client.clickY >= 463 && client.clickY <= 499) {
               if (musicVolumeSetting != 0) {
                  stopMidi(false);
                  client.previousSong = 0;
                  client.varps[168] = 4;
                  client.applyVarpSetting(168);
               } else {
                  stopMidi(false);
                  client.previousSong = 0;
                  client.varps[168] = 0;
                  client.applyVarpSetting(168);
                  client.updateSeasonalTheme();
                  client.requestMusicTrackWithFade(18, musicVolumeSetting, client.customSettingShowExperiencePerHourStartLevels);
               }

               client.welcomeScreenRaised = true;
            }

            if (client.loginScreenState == 0) {
               int localCanvasWidth = client.canvasWidth / 2 - 80;
               int localCanvasHeight = client.canvasHeight / 2 + 20;
               localCanvasHeight += 20;
               if (client.clickButton == 1
                  && client.clickX >= localCanvasWidth - 75
                  && client.clickX <= localCanvasWidth + 75
                  && client.clickY >= localCanvasHeight - 20
                  && client.clickY <= localCanvasHeight + 20) {
                  client.loginScreenState = 3;
                  client.loginScreenCursorPos = 0;
               }

               localCanvasWidth = client.canvasWidth / 2 + 80;
               if (client.clickButton == 1
                  && client.clickX >= localCanvasWidth - 75
                  && client.clickX <= localCanvasWidth + 75
                  && client.clickY >= localCanvasHeight - 20
                  && client.clickY <= localCanvasHeight + 20) {
                  client.loginMessage1 = "";
                  client.loginMessage2 = "Enter your username & password.";
                  client.loginScreenState = 2;
                  client.loginScreenCursorPos = 0;
               }
            } else if (client.loginScreenState != 2) {
               if (client.loginScreenState == 3) {
                  int canvasWidth2 = client.canvasWidth / 2;
                  int canvasHeight2 = client.canvasHeight / 2 + 50;
                  canvasHeight2 += 20;
                  if (client.clickButton == 1
                     && client.clickX >= canvasWidth2 - 75
                     && client.clickX <= canvasWidth2 + 75
                     && client.clickY >= canvasHeight2 - 20
                     && client.clickY <= canvasHeight2 + 20) {
                     client.loginScreenState = 0;
                  }
               }
            } else {
               processLoginInput: {
                  int canvasHeight3 = client.canvasHeight / 2 - 40;
                  canvasHeight3 += 30;
                  canvasHeight3 += 25;
                  if (client.clickButton == 1 && client.clickY >= canvasHeight3 - 15 && client.clickY < canvasHeight3) {
                     client.loginScreenCursorPos = 0;
                  }

                  canvasHeight3 += 15;
                  if (client.clickButton == 1 && client.clickY >= canvasHeight3 - 15 && client.clickY < canvasHeight3) {
                     client.loginScreenCursorPos = 1;
                  }

                  int canvasWidth3 = client.canvasWidth / 2 - 80;
                  int canvasHeight4 = client.canvasHeight / 2 + 50;
                  canvasHeight4 += 20;
                  if (client.clickButton == 1
                     && client.clickX >= canvasWidth3 - 75
                     && client.clickX <= canvasWidth3 + 75
                     && client.clickY >= canvasHeight4 - 20
                     && client.clickY <= canvasHeight4 + 20) {
                     client.loginFailures = 0;
                     client.login(username, password, false);
                     if (loggedIn) {
                        break processLoginInput;
                     }
                  }

                  canvasWidth3 = client.canvasWidth / 2 + 80;
                  if (client.clickButton == 1
                     && client.clickX >= canvasWidth3 - 75
                     && client.clickX <= canvasWidth3 + 75
                     && client.clickY >= canvasHeight4 - 20
                     && client.clickY <= canvasHeight4 + 20) {
                     client.loginScreenState = 0;
                  }

                  int readCharResult;
                   while ((readCharResult = client.readChar(-796)) != -1) {
                     boolean localFlag = false;

                     for (int loopIndex = 0; loopIndex < 95; loopIndex++) {
                        if (readCharResult == "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"£$%^&*()-_=+[{]};:'@#~,<.>/?\\| ".charAt(loopIndex)) {
                           localFlag = true;
                           break;
                        }
                     }

                     if (client.loginScreenCursorPos == 0) {
                        if (readCharResult == 8 && username.length() > 0) {
                           username = username.substring(0, username.length() - 1);
                        }

                        if (readCharResult == 9 || readCharResult == 10 || readCharResult == 13) {
                           client.loginScreenCursorPos = 1;
                        }

                        if (localFlag) {
                           username = username + (char)readCharResult;
                        }

                        if (username.length() > 12) {
                           username = username.substring(0, 12);
                        }
                     } else if (client.loginScreenCursorPos == 1) {
                        if (readCharResult == 8 && password.length() > 0) {
                           password = password.substring(0, password.length() - 1);
                        }

                        if (readCharResult == 9 || readCharResult == 10 || readCharResult == 13) {
                           client.loginScreenCursorPos = 0;
                        }

                        if (localFlag) {
                           password = password + (char)readCharResult;
                        }

                        if (password.length() > 20) {
                           password = password.substring(0, 20);
                        }
                     }
                  }
               }
            }
         }

         this.processOnDemandQueue();
      }
   }
   private void addLocalAndTargetPlayerToScene() {
      if (localPlayer.worldX >> 7 == this.destX && localPlayer.worldY >> 7 == this.destY) {
         this.destX = 0;
      }

      this.addPlayerToScene(localPlayer, 33538048, true);
      int playerIndex;
      if ((playerIndex = localPlayer.interactingEntity - 32768) > 0) {
         Player player = this.players[playerIndex];
         this.addPlayerToScene(player, playerIndex << 14, false);
      }
   }
   private void addOtherPlayersToScene() {
      for (int playerIndex = 0; playerIndex < this.playerCount; playerIndex++) {
         Player player = this.players[this.playerIndices[playerIndex]];
         int localPlayerIndices = this.playerIndices[playerIndex] << 14;
         int localInteractingEntity;
         if ((localInteractingEntity = localPlayer.interactingEntity - 32768) <= 0 || localPlayerIndices != localInteractingEntity << 14) {
            this.addPlayerToScene(player, localPlayerIndices, false);
         }
      }
   }
   private boolean addPlayerToScene(Player player, int scalarArgument, boolean flag) {
      if (player != null && player.visible) {
         player.unanimated = this.playerCount > 200 && !flag && player.movementAnimation == player.idleAnimationId;
         int tileCycleMarkerIndex = player.worldX >> 7;
         int localWorldY = player.worldY >> 7;
         if (tileCycleMarkerIndex < 0 || tileCycleMarkerIndex >= 104 || localWorldY < 0 || localWorldY >= 104) {
            return false;
         }

         if (player.attachedModel != null && gameCycle >= player.attachedModelStartCycle && gameCycle < player.attachedModelEndCycle) {
            player.unanimated = false;
            player.attachedModelBaseHeight = this.getTileHeight(this.plane, player.worldY, player.worldX);
            this.scene
               .addEntityBounds(
                  this.plane, player.worldY, player, player.orientation, player.attachedModelMaxY, player.worldX, player.attachedModelBaseHeight, player.attachedModelMinX, player.attachedModelMaxX, scalarArgument, player.attachedModelMinY
               );
            return false;
         }

         if ((player.worldX & 127) == 64 && (player.worldY & 127) == 64) {
            if (this.tileCycleMarkers[tileCycleMarkerIndex][localWorldY] == this.sceneCycle) {
               return false;
            }

            this.tileCycleMarkers[tileCycleMarkerIndex][localWorldY] = this.sceneCycle;
         }

         player.attachedModelBaseHeight = this.getTileHeight(this.plane, player.worldY, player.worldX);
         this.scene.addEntityWithRadius(this.plane, player.orientation, player.attachedModelBaseHeight, scalarArgument, player.worldY, 60, player.worldX, player, player.animationStretches);
         return true;
      } else {
         return false;
      }
   }
   private boolean promptUserForInput(Widget widget) {
      int contentType = widget.contentType;
      if (this.friendServerStatus == 2) {
         if (contentType == 201) {
            this.inputTaken = true;
            this.inputDialogState = 0;
            this.messagePromptRaised = true;
            this.promptInput = "";
            this.friendsListAction = 1;
            this.promptMessage = "Enter name of friend to add to list";
         }

         if (contentType == 202) {
            this.inputTaken = true;
            this.inputDialogState = 0;
            this.messagePromptRaised = true;
            this.promptInput = "";
            this.friendsListAction = 2;
            this.promptMessage = "Enter name of friend to delete from list";
         }
      }

      if (contentType == 205) {
         this.logoutTimer = 250;
         return true;
      }

      if (contentType == 501) {
         this.inputTaken = true;
         this.inputDialogState = 0;
         this.messagePromptRaised = true;
         this.promptInput = "";
         this.friendsListAction = 4;
         this.promptMessage = "Enter name of player to add to list";
      }

      if (contentType == 502) {
         this.inputTaken = true;
         this.inputDialogState = 0;
         this.messagePromptRaised = true;
         this.promptInput = "";
         this.friendsListAction = 5;
         this.promptMessage = "Enter name of player to delete from list";
      }

      if (contentType >= 300 && contentType <= 313) {
         int characterDesignKitIdIndex2 = (contentType - 300) / 2;
         int scalar = contentType & 1;
         int kitIndex;
         if ((kitIndex = this.characterDesignKitIds[characterDesignKitIdIndex2]) != -1) {
            do {
               if (scalar == 0) {
                  if (--kitIndex < 0) {
                     kitIndex = IdentityKit.length - 1;
                  }
               }

               if (scalar == 1) {
                  if (++kitIndex >= IdentityKit.length) {
                     kitIndex = 0;
                  }
               }
            } while (IdentityKit.kits[kitIndex].nonSelectable || IdentityKit.kits[kitIndex].bodyPartId != characterDesignKitIdIndex2 + (this.maleCharacter ? 0 : 7));

            this.characterDesignKitIds[characterDesignKitIdIndex2] = kitIndex;
            this.characterDesignNeedsRebuild = true;
         }
      }

      if (contentType >= 314 && contentType <= 323) {
         int characterDesignColourIndex2 = (contentType - 314) / 2;
         int scalar2 = contentType & 1;
         int characterDesignColourOrCharacterDesignColours = this.characterDesignColours[characterDesignColourIndex2];
         if (scalar2 == 0) {
            if (--characterDesignColourOrCharacterDesignColours < 0) {
               characterDesignColourOrCharacterDesignColours = playerBodyColors[characterDesignColourIndex2].length - 1;
            }
         }

         if (scalar2 == 1) {
            if (++characterDesignColourOrCharacterDesignColours >= playerBodyColors[characterDesignColourIndex2].length) {
               characterDesignColourOrCharacterDesignColours = 0;
            }
         }

         this.characterDesignColours[characterDesignColourIndex2] = characterDesignColourOrCharacterDesignColours;
         this.characterDesignNeedsRebuild = true;
      }

      if (contentType == 324 && !this.maleCharacter) {
         this.maleCharacter = true;
         this.resetCharacterDesign();
      }

      if (contentType == 325 && this.maleCharacter) {
         this.maleCharacter = false;
         this.resetCharacterDesign();
      }

      if (contentType != 326) {
         if (contentType == 620) {
            this.canMute = !this.canMute;
         }

         if (contentType >= 601 && contentType <= 613) {
            this.closeTopInterfaces();
            if (this.reportAbuseInput.length() > 0) {
               this.outgoingBuffer.writeOpcode(218);
               this.outgoingBuffer.writeLong(NameUtils.encodeBase37(this.reportAbuseInput));
               this.outgoingBuffer.writeByte(contentType - 601);
               this.outgoingBuffer.writeByte(this.canMute ? 1 : 0);
            }
         }

         return false;
      } else {
         this.outgoingBuffer.writeOpcode(101);
         this.outgoingBuffer.writeByte(this.maleCharacter ? 0 : 1);

         for (int characterDesignKitIdIndex = 0; characterDesignKitIdIndex < 7; characterDesignKitIdIndex++) {
            this.outgoingBuffer.writeByte(this.characterDesignKitIds[characterDesignKitIdIndex]);
         }

         for (int characterDesignColourIndex = 0; characterDesignColourIndex < 5; characterDesignColourIndex++) {
            this.outgoingBuffer.writeByte(this.characterDesignColours[characterDesignColourIndex]);
         }

         return true;
      }
   }
   private void parsePlayerUpdateMasks(Buffer newBuffer, int packetSize) {
      for (int entityUpdateIndex2 = 0; entityUpdateIndex2 < this.entityUpdateCount; entityUpdateIndex2++) {
         int entityUpdateIndex = this.entityUpdateIndices[entityUpdateIndex2];
         Player player = this.players[entityUpdateIndex];
         int updateStart = newBuffer.currentPosition;
         if (updateStart >= packetSize) {
            System.err.println("P81 update overflow before block " + (entityUpdateIndex2 + 1) + "/" + this.entityUpdateCount
               + " player=" + entityUpdateIndex + " pos=" + updateStart + " psize=" + packetSize);
            return;
         }
         int decodedUnsignedByte;
         if (((decodedUnsignedByte = newBuffer.readUnsignedByte()) & 64) != 0) {
            decodedUnsignedByte += newBuffer.readUnsignedByte() << 8;
         }

         Player sourcePlayer = player;
         if (false) System.err.println("P81 update " + (entityUpdateIndex2 + 1) + "/" + this.entityUpdateCount + " player=" + entityUpdateIndex
            + " mask=0x" + Integer.toHexString(decodedUnsignedByte) + " start=" + updateStart
            + " payload=" + newBuffer.currentPosition + " psize=" + packetSize);

         Buffer buffer = newBuffer;
         int sourceEntityUpdateIndex = entityUpdateIndex;
         int sourceDecodedUnsignedByte = decodedUnsignedByte;
         Client client = this;
         if ((sourceDecodedUnsignedByte & 1024) != 0) {
            sourcePlayer.forceMoveStartX = buffer.readUnsignedByteSubtracted();
            sourcePlayer.forceMoveStartY = buffer.readUnsignedByteSubtracted();
            sourcePlayer.forceMoveEndX = buffer.readUnsignedByteSubtracted();
            sourcePlayer.forceMoveEndY = buffer.readUnsignedByteSubtracted();
            sourcePlayer.forceMoveStartCycle = buffer.readUnsignedShortLittleEndianAdded() + gameCycle;
            sourcePlayer.forceMoveEndCycle = buffer.readUnsignedShortAdded() + gameCycle;
            sourcePlayer.forceMoveFaceDirection = buffer.readUnsignedByteSubtracted();
            sourcePlayer.resetPath();
         }

         if ((sourceDecodedUnsignedByte & 256) != 0) {
            sourcePlayer.graphicId = buffer.readUnsignedShortLittleEndian();
            int graphicHeightOrReadInt = buffer.readInt();
            sourcePlayer.graphicHeight = graphicHeightOrReadInt >> 16;
            if (hdModels && (sourcePlayer.graphicId == 112 || sourcePlayer.graphicId == 113)) {
               sourcePlayer.turnAroundAnimationId = 0;
            }

            int scalar = graphicHeightOrReadInt & 65535;
            sourcePlayer.graphicDelay = gameCycle + scalar;
            sourcePlayer.graphicFrame = 0;
            sourcePlayer.graphicFrameCycle = 0;
            if (sourcePlayer.graphicDelay > gameCycle) {
               sourcePlayer.graphicFrame = -1;
            }

            if (sourcePlayer.graphicId == 65535) {
               sourcePlayer.graphicId = -1;
            }

            int localRemapId = -1;
            if (sourcePlayer.graphicId != -1) {
               localRemapId = AnimationSequence.remapId(SpotAnimationDefinition.definitions[sourcePlayer.graphicId].animationSequence.frameIds[0] >> 16);
            }

            if ((hdModels || !hdModels && extendedRevisionEnabled && (localRemapId > 1043 || use2007Models || extendedModelIds.contains(localRemapId))) && localRemapId != -1) {
               try {
                  if (AnimationFrame.frameCache.get(localRemapId) == null) {
                     client.onDemandFetcher.provide(1, localRemapId);
                  }
               } catch (Exception exception) {
               }
            }
         }

         if ((sourceDecodedUnsignedByte & 8) != 0) {
            int emoteAnimationOrReadUnsignedShortLittleEndian;
            if ((emoteAnimationOrReadUnsignedShortLittleEndian = buffer.readUnsignedShortLittleEndian()) == 65535) {
               emoteAnimationOrReadUnsignedShortLittleEndian = -1;
            }

            int animationDelayOrReadUnsignedByteNegated = buffer.readUnsignedByteNegated();
            if (emoteAnimationOrReadUnsignedShortLittleEndian == sourcePlayer.emoteAnimation && emoteAnimationOrReadUnsignedShortLittleEndian != -1) {
               int localSequences;
               if ((localSequences = AnimationSequence.sequences[AnimationSequence.remapId(emoteAnimationOrReadUnsignedShortLittleEndian)].replyMode) == 1) {
                  sourcePlayer.emoteFrame = 0;
                  sourcePlayer.emoteFrameCycle = 0;
                  sourcePlayer.animationDelay = animationDelayOrReadUnsignedByteNegated;
                  sourcePlayer.animationLoopCount = 0;
               }

               if (localSequences == 2) {
                  sourcePlayer.animationLoopCount = 0;
               }
            } else if (emoteAnimationOrReadUnsignedShortLittleEndian == -1
               || sourcePlayer.emoteAnimation == -1
               || AnimationSequence.sequences[AnimationSequence.remapId(emoteAnimationOrReadUnsignedShortLittleEndian)].forcedPriority >= AnimationSequence.sequences[AnimationSequence.remapId(sourcePlayer.emoteAnimation)].forcedPriority) {
               sourcePlayer.emoteAnimation = emoteAnimationOrReadUnsignedShortLittleEndian;
               sourcePlayer.emoteFrame = 0;
               sourcePlayer.emoteFrameCycle = 0;
               sourcePlayer.animationDelay = animationDelayOrReadUnsignedByteNegated;
               sourcePlayer.animationLoopCount = 0;
               sourcePlayer.emotePathLength = sourcePlayer.pathLength;
            }
         }

         if ((sourceDecodedUnsignedByte & 4) != 0) {
            sourcePlayer.spokenText = buffer.readString();
            if (sourcePlayer.spokenText.charAt(0) == '~') {
               sourcePlayer.spokenText = sourcePlayer.spokenText.substring(1);
               client.pushMessage(sourcePlayer.spokenText, 2, sourcePlayer.name, 0, 0, 0);
            } else if (sourcePlayer == localPlayer) {
               client.pushMessage(sourcePlayer.spokenText, 2, sourcePlayer.name, 0, 0, 0);
            }

            sourcePlayer.textColor = 0;
            sourcePlayer.textEffect = 0;
            sourcePlayer.textCycle = 150;
         }

         if ((sourceDecodedUnsignedByte & 128) != 0) {
            int textColorOrReadUnsignedShortLittleEndian = buffer.readUnsignedShortLittleEndian();
            int privelage = sourcePlayer.privelage;
            int length = buffer.readUnsignedByteNegated();
            int currentPosition = buffer.currentPosition;
            if (sourcePlayer.name != null && sourcePlayer.visible) {
               long encodedName = NameUtils.encodeBase37(sourcePlayer.name);
               boolean flag = false;
               if (privelage <= 1) {
                  for (int ignoreListAsLongIndex = 0; ignoreListAsLongIndex < client.ignoreCount; ignoreListAsLongIndex++) {
                     if (client.ignoreListAsLongs[ignoreListAsLongIndex] == encodedName) {
                        flag = true;
                        break;
                     }
                  }
               }

               if (!flag && client.onTutorialIsland == 0) {
                  try {
                     client.chatBuffer.currentPosition = 0;
                     byte[] buffer4 = client.chatBuffer.buffer;
                     flag = false;
                     int sourceLength = length;
                     Buffer buffer2 = buffer;

                     for (int loopIndex = sourceLength + 0 - 1; loopIndex >= 0; loopIndex--) {
                        buffer4[loopIndex] = buffer2.buffer[buffer2.currentPosition++];
                     }

                     client.chatBuffer.currentPosition = 0;
                     String text = ChatFilter.apply(ChatCodec.decode(length, client.chatBuffer));
                     sourcePlayer.spokenText = text;
                     sourcePlayer.textColor = textColorOrReadUnsignedShortLittleEndian >> 8;
                     sourcePlayer.textEffect = textColorOrReadUnsignedShortLittleEndian & 0xFF;
                     sourcePlayer.textCycle = 150;
                     if (privelage != 2 && privelage != 3) {
                        client.pushMessage(text, 2, sourcePlayer.name, sourcePlayer.privelage, sourcePlayer.donatorStatus, sourcePlayer.accountMode);
                     } else {
                        client.pushMessage(text, 1, sourcePlayer.name, 2, 0, 0);
                     }
                  } catch (Exception exception2) {
                     SignLink.reporterror("cde2");
                  }
               }
            }

            buffer.currentPosition = currentPosition + length;
         }

         if ((sourceDecodedUnsignedByte & 1) != 0) {
            sourcePlayer.interactingEntity = buffer.readUnsignedShortLittleEndian();
            if (sourcePlayer.interactingEntity == 65535) {
               sourcePlayer.interactingEntity = -1;
            }
         }

         if ((sourceDecodedUnsignedByte & 16) != 0) {
            int readUnsignedByteNegated2;
            byte[] byteBuffer = new byte[readUnsignedByteNegated2 = buffer.readUnsignedByteNegated()];
            Buffer buffer3 = new Buffer(byteBuffer);
            buffer.readBytes(readUnsignedByteNegated2, 0, byteBuffer);
            client.playerAppearanceBuffers[sourceEntityUpdateIndex] = buffer3;
            sourcePlayer.updatePlayer(buffer3);
         }

         if ((sourceDecodedUnsignedByte & 2) != 0) {
            sourcePlayer.faceX = buffer.readUnsignedShortLittleEndianAdded();
            sourcePlayer.faceY = buffer.readUnsignedShortLittleEndian();
         }

         if ((sourceDecodedUnsignedByte & 32) != 0) {
            int readUnsignedByte2 = buffer.readUnsignedByte();
            int readUnsignedByte3 = buffer.readUnsignedByte();
            int hitType = buffer.readUnsignedByteAdded();
            sourcePlayer.addHit(hitType, readUnsignedByte2, readUnsignedByte3, gameCycle);
            sourcePlayer.healthBarEndCycle = gameCycle + 300;
            sourcePlayer.currentHealth = buffer.readUnsignedShort();
            sourcePlayer.maxHealth = buffer.readUnsignedShort();
         }

         if ((sourceDecodedUnsignedByte & 512) != 0) {
            int readUnsignedByte4 = buffer.readUnsignedByte();
            int readUnsignedByte5 = buffer.readUnsignedByte();
            int decodedUnsignedByteSubtracted = buffer.readUnsignedByteSubtracted();
            sourcePlayer.addHit(decodedUnsignedByteSubtracted, readUnsignedByte4, readUnsignedByte5, gameCycle);
            sourcePlayer.healthBarEndCycle = gameCycle + 300;
            sourcePlayer.currentHealth = buffer.readUnsignedShort();
            sourcePlayer.maxHealth = buffer.readUnsignedShort();
         }
      }
   }
   private void drawMapScenes(int tileY, int sourcePixelIndex, int clippingDataIndex, int pixelIndex, int tileIndex) {
      int localScene;
      if ((localScene = this.scene.getWallHash(tileIndex, clippingDataIndex, tileY)) != 0) {
         int scene2;
         int scalar = (scene2 = this.scene.getArrangement(tileIndex, clippingDataIndex, tileY, localScene)) >> 6 & 3;
         scene2 &= 31;
         if (localScene > 0) {
            sourcePixelIndex = pixelIndex;
         }

         int[] pixels = this.minimapImage.pixels;
         pixelIndex = 24624 + (clippingDataIndex << 2) + (103 - tileY << 9 << 2);
         ObjectDefinition objectDefinition;
         if ((objectDefinition = ObjectDefinition.lookup(localScene >> 14 & 32767)).mapSceneId != -1) {
            IndexedSprite indexedSprite;
            if ((indexedSprite = this.mapSceneSprites[objectDefinition.mapSceneId]) != null) {
               sourcePixelIndex = ((objectDefinition.sizeX << 2) - indexedSprite.width) / 2;
               pixelIndex = ((objectDefinition.sizeY << 2) - indexedSprite.height) / 2;
               indexedSprite.drawBackground(48 + (clippingDataIndex << 2) + sourcePixelIndex, 48 + (104 - tileY - objectDefinition.sizeY << 2) + pixelIndex);
            }
         } else {
            if (scene2 == 0 || scene2 == 2) {
               if (scalar == 0) {
                  pixels[pixelIndex] = sourcePixelIndex;
                  pixels[pixelIndex + 512] = sourcePixelIndex;
                  pixels[pixelIndex + 1024] = sourcePixelIndex;
                  pixels[pixelIndex + 1536] = sourcePixelIndex;
               } else if (scalar == 1) {
                  pixels[pixelIndex] = sourcePixelIndex;
                  pixels[pixelIndex + 1] = sourcePixelIndex;
                  pixels[pixelIndex + 2] = sourcePixelIndex;
                  pixels[pixelIndex + 3] = sourcePixelIndex;
               } else if (scalar == 2) {
                  pixels[pixelIndex + 3] = sourcePixelIndex;
                  pixels[pixelIndex + 3 + 512] = sourcePixelIndex;
                  pixels[pixelIndex + 3 + 1024] = sourcePixelIndex;
                  pixels[pixelIndex + 3 + 1536] = sourcePixelIndex;
               } else if (scalar == 3) {
                  pixels[pixelIndex + 1536] = sourcePixelIndex;
                  pixels[pixelIndex + 1536 + 1] = sourcePixelIndex;
                  pixels[pixelIndex + 1536 + 2] = sourcePixelIndex;
                  pixels[pixelIndex + 1536 + 3] = sourcePixelIndex;
               }
            }

            if (scene2 == 3) {
               if (scalar == 0) {
                  pixels[pixelIndex] = sourcePixelIndex;
               } else if (scalar == 1) {
                  pixels[pixelIndex + 3] = sourcePixelIndex;
               } else if (scalar == 2) {
                  pixels[pixelIndex + 3 + 1536] = sourcePixelIndex;
               } else if (scalar == 3) {
                  pixels[pixelIndex + 1536] = sourcePixelIndex;
               }
            }

            if (scene2 == 2) {
               if (scalar == 3) {
                  pixels[pixelIndex] = sourcePixelIndex;
                  pixels[pixelIndex + 512] = sourcePixelIndex;
                  pixels[pixelIndex + 1024] = sourcePixelIndex;
                  pixels[pixelIndex + 1536] = sourcePixelIndex;
               } else if (scalar == 0) {
                  pixels[pixelIndex] = sourcePixelIndex;
                  pixels[pixelIndex + 1] = sourcePixelIndex;
                  pixels[pixelIndex + 2] = sourcePixelIndex;
                  pixels[pixelIndex + 3] = sourcePixelIndex;
               } else if (scalar == 1) {
                  pixels[pixelIndex + 3] = sourcePixelIndex;
                  pixels[pixelIndex + 3 + 512] = sourcePixelIndex;
                  pixels[pixelIndex + 3 + 1024] = sourcePixelIndex;
                  pixels[pixelIndex + 3 + 1536] = sourcePixelIndex;
               } else if (scalar == 2) {
                  pixels[pixelIndex + 1536] = sourcePixelIndex;
                  pixels[pixelIndex + 1536 + 1] = sourcePixelIndex;
                  pixels[pixelIndex + 1536 + 2] = sourcePixelIndex;
                  pixels[pixelIndex + 1536 + 3] = sourcePixelIndex;
               }
            }
         }
      }

      if ((localScene = this.scene.getInteractiveObjectHash(tileIndex, clippingDataIndex, tileY)) != 0) {
         int scene3;
         int scalar2 = (scene3 = this.scene.getArrangement(tileIndex, clippingDataIndex, tileY, localScene)) >> 6 & 3;
         scene3 &= 31;
         ObjectDefinition objectDefinition3;
         if ((objectDefinition3 = ObjectDefinition.lookup(localScene >> 14 & 32767)).mapSceneId != -1) {
            IndexedSprite indexedSprite3;
            if ((indexedSprite3 = this.mapSceneSprites[objectDefinition3.mapSceneId]) != null) {
               localScene = ((objectDefinition3.sizeX << 2) - indexedSprite3.width) / 2;
               int scalar3 = ((objectDefinition3.sizeY << 2) - indexedSprite3.height) / 2;
               indexedSprite3.drawBackground(48 + (clippingDataIndex << 2) + localScene, 48 + (104 - tileY - objectDefinition3.sizeY << 2) + scalar3);
            }
         } else if (scene3 == 9) {
            pixelIndex = 15658734;
            if (localScene > 0) {
               pixelIndex = 15597568;
            }

            int[] minimapImage2 = this.minimapImage.pixels;
            int position = 24624 + (clippingDataIndex << 2) + (103 - tileY << 9 << 2);
            if (scalar2 != 0 && scalar2 != 2) {
               minimapImage2[position] = pixelIndex;
               minimapImage2[position + 512 + 1] = pixelIndex;
               minimapImage2[position + 1024 + 2] = pixelIndex;
               minimapImage2[position + 1536 + 3] = pixelIndex;
            } else {
               minimapImage2[position + 1536] = pixelIndex;
               minimapImage2[position + 1024 + 1] = pixelIndex;
               minimapImage2[position + 512 + 2] = pixelIndex;
               minimapImage2[position + 3] = pixelIndex;
            }
         }
      }

      ObjectDefinition objectDefinition2;
      IndexedSprite indexedSprite2;
      if ((localScene = this.scene.getFloorDecorationHash(tileIndex, clippingDataIndex, tileY)) != 0
         && (objectDefinition2 = ObjectDefinition.lookup(localScene >> 14 & 32767)).mapSceneId != -1
         && (indexedSprite2 = this.mapSceneSprites[objectDefinition2.mapSceneId]) != null) {
         sourcePixelIndex = ((objectDefinition2.sizeX << 2) - indexedSprite2.width) / 2;
         int scalar4 = ((objectDefinition2.sizeY << 2) - indexedSprite2.height) / 2;
         indexedSprite2.drawBackground(48 + (clippingDataIndex << 2) + sourcePixelIndex, 48 + (104 - tileY - objectDefinition2.sizeY << 2) + scalar4);
      }
   }
   private static void stopMidi(boolean flag) {
      if (isMidiPlayerAvailable()) {
         flag = false;
         playMidiTrack(0, null, flag);
         musicRequestPending = false;
      }
   }
   private void loadTitleScreen() {
      this.titleBox = new IndexedSprite(this.titleArchive, "titlebox", 0);
      this.titleButton = new IndexedSprite(this.titleArchive, "titlebutton", 0);
      this.titleRuneSprites = new IndexedSprite[12];
      int localParseInt = 0;

      try {
         localParseInt = Integer.parseInt(this.getParameter("fl_icon"));
      } catch (Exception exception) {
      }

      if (localParseInt == 0) {
         for (int titleRuneSpriteIndex2 = 0; titleRuneSpriteIndex2 < 12; titleRuneSpriteIndex2++) {
            this.titleRuneSprites[titleRuneSpriteIndex2] = new IndexedSprite(this.titleArchive, "runes", titleRuneSpriteIndex2);
         }
      } else {
         for (int titleRuneSpriteIndex = 0; titleRuneSpriteIndex < 12; titleRuneSpriteIndex++) {
            this.titleRuneSprites[titleRuneSpriteIndex] = new IndexedSprite(this.titleArchive, "runes", 12 + (titleRuneSpriteIndex & 3));
         }
      }

      this.originalFlameRightBackground = new Sprite(128, 265);
      this.originalBottomLeftBackground = new Sprite(128, 265);
      System.arraycopy(this.flameRightBackground.pixels, 0, this.originalFlameRightBackground.pixels, 0, 33920);
      System.arraycopy(this.bottomLeft0BackgroundTile.pixels, 0, this.originalBottomLeftBackground.pixels, 0, 33920);
      this.warmFlamePalette = new int[256];

      for (int warmFlamePaletteIndex = 0; warmFlamePaletteIndex < 64; warmFlamePaletteIndex++) {
         this.warmFlamePalette[warmFlamePaletteIndex] = warmFlamePaletteIndex * 262144;
      }

      for (int loopIndex = 0; loopIndex < 64; loopIndex++) {
         this.warmFlamePalette[loopIndex + 64] = 16711680 + loopIndex * 1024;
      }

      for (int loopIndex2 = 0; loopIndex2 < 64; loopIndex2++) {
         this.warmFlamePalette[loopIndex2 + 128] = 16776960 + 4 * loopIndex2;
      }

      for (int loopIndex3 = 0; loopIndex3 < 64; loopIndex3++) {
         this.warmFlamePalette[loopIndex3 + 192] = 16777215;
      }

      this.greenFlamePalette = new int[256];

      for (int greenFlamePaletteIndex = 0; greenFlamePaletteIndex < 64; greenFlamePaletteIndex++) {
         this.greenFlamePalette[greenFlamePaletteIndex] = greenFlamePaletteIndex << 10;
      }

      for (int loopIndex4 = 0; loopIndex4 < 64; loopIndex4++) {
         this.greenFlamePalette[loopIndex4 + 64] = 65280 + 4 * loopIndex4;
      }

      for (int loopIndex5 = 0; loopIndex5 < 64; loopIndex5++) {
         this.greenFlamePalette[loopIndex5 + 128] = 65535 + loopIndex5 * 262144;
      }

      for (int loopIndex6 = 0; loopIndex6 < 64; loopIndex6++) {
         this.greenFlamePalette[loopIndex6 + 192] = 16777215;
      }

      this.blueFlamePalette = new int[256];

      for (int blueFlamePaletteIndex = 0; blueFlamePaletteIndex < 64; blueFlamePaletteIndex++) {
         this.blueFlamePalette[blueFlamePaletteIndex] = blueFlamePaletteIndex << 2;
      }

      for (int loopIndex7 = 0; loopIndex7 < 64; loopIndex7++) {
         this.blueFlamePalette[loopIndex7 + 64] = 255 + loopIndex7 * 262144;
      }

      for (int loopIndex8 = 0; loopIndex8 < 64; loopIndex8++) {
         this.blueFlamePalette[loopIndex8 + 128] = 16711935 + loopIndex8 * 1024;
      }

      for (int loopIndex9 = 0; loopIndex9 < 64; loopIndex9++) {
         this.blueFlamePalette[loopIndex9 + 192] = 16777215;
      }

      this.activeFlamePalette = new int[256];
      this.flameNoise = new int[32768];
      this.flameNoiseScratch = new int[32768];
      this.randomizeBackground(null);
      this.flameIntensity = new int[32768];
      this.flameIntensityScratch = new int[32768];
      this.drawLoadingText(10, "Connecting to fileserver");
      if (!this.flameThreadRunning) {
         this.drawFlames = true;
         this.flameThreadRunning = true;
         this.startRunnable(this, 2);
      }
   }
   private static void sleepMillis(long longScalarArgument) {
      try {
         Thread.sleep(longScalarArgument);
      } catch (InterruptedException exception) {
      }
   }
   private static void setHighMemoryMode() {
      SceneGraph.lowMemory = false;
      Rasterizer3D.lowMemory = false;
      lowMem = false;
      RegionBuilder.lowMemory = false;
      ObjectDefinition.lowMemory = false;
   }

   public static void main(String[] text) {
      try {
         nodeID = 10;
         portOff = 0;
         setHighMemoryMode();
         isMembers = true;
         SignLink.storeId = 32;
         SignLink.startpriv(InetAddress.getLocalHost());

         try {
            loadUserConfig();
         } catch (IOException exception) {
         }

         if (useDesktopWindow) {
            clientInstance = new ClientWindow();
         } else {
            Client client = clientInstance = new Client();

            try {
               try {
                  SignLink.startpriv(InetAddress.getByName(serverAddress));
               } catch (Exception exception6) {
               }

               try {
                  SpriteArchive.loadSprites();
                  SpriteArchive.loadInterfaceSprites();
                  customSprites = SpriteArchive.sprites;
                  interfaceSprites = SpriteArchive.interfaceSprites;
               } catch (Exception exception2) {
                  System.out.println("Failed to load custom sprites.");
               }

               clientInstance = client;
            } catch (Exception exception3) {
               exception3.printStackTrace();
            }

            try {
               loadDisplaySettings();
            } catch (IOException exception4) {
            }

            short shortCode = 765;
            short shortCode2 = 503;
            Client sourceClientInstance = clientInstance;
            clientInstance.canvasWidth = 765;
            sourceClientInstance.canvasHeight = 503;
            sourceClientInstance.gameFrame = new GameFrame(sourceClientInstance, sourceClientInstance.canvasWidth, sourceClientInstance.canvasHeight);
            sourceClientInstance.graphics = sourceClientInstance.getGameComponent().getGraphics();
            int canvasWidth = sourceClientInstance.canvasWidth;
            int canvasHeight = sourceClientInstance.canvasHeight;
            sourceClientInstance.getGameComponent();
            sourceClientInstance.graphicsBuffer = new BufferedImageGraphicsBuffer(canvasWidth, canvasHeight);
            sourceClientInstance.startRunnable(sourceClientInstance, 1);
         }
      } catch (Exception exception5) {
      }
   }
   private void updateProjectiles() {
      for (Projectile projectile = (Projectile)this.projectiles.first(); projectile != null; projectile = (Projectile)this.projectiles.next()) {
         if (projectile.plane != this.plane || gameCycle > projectile.endCycle) {
            projectile.unlink();
         } else if (gameCycle >= projectile.startCycle) {
            Npc npc;
            if (projectile.targetIndex > 0 && (npc = this.npcs[projectile.targetIndex - 1]) != null && npc.worldX >= 0 && npc.worldX < 13312 && npc.worldY >= 0 && npc.worldY < 13312) {
               projectile.trackTarget(gameCycle, npc.worldY, this.getTileHeight(projectile.plane, npc.worldY, npc.worldX) - projectile.endHeight, npc.worldX);
            }

            if (projectile.targetIndex < 0) {
               int playerIndex;
               Player player;
               if ((playerIndex = -projectile.targetIndex - 1) == this.localPlayerIndex) {
                  player = localPlayer;
               } else {
                  player = this.players[playerIndex];
               }

               if (player != null && player.worldX >= 0 && player.worldX < 13312 && player.worldY >= 0 && player.worldY < 13312) {
                  projectile.trackTarget(gameCycle, player.worldY, this.getTileHeight(projectile.plane, player.worldY, player.worldX) - projectile.endHeight, player.worldX);
               }
            }

            projectile.move(this.animationCycleDelta);
            this.scene.addEntityWithRadius(this.plane, projectile.yaw, (int)projectile.currentHeight, -1, (int)projectile.currentY, 60, (int)projectile.currentX, projectile, false);
         }
      }
   }

   @Override
   public AppletContext getAppletContext() {
      return super.getAppletContext();
   }
   private void drawLogo() {
      this.updateSeasonalTheme();
      String text = "title.dat";
      if (this.setChannel > 0) {
         text = "title" + this.setChannel + ".dat";
      }

      byte[] file = this.titleArchive.getFile(text);
      Sprite sprite = new Sprite(file, this);
      this.flameRightBackground.initDrawingArea();
      sprite.drawOpaqueSprite(0, 0);
      this.bottomLeft0BackgroundTile.initDrawingArea();
      sprite.drawOpaqueSprite(-637, 0);
      this.topLeft1BackgroundTile.initDrawingArea();
      sprite.drawOpaqueSprite(-128, 0);
      this.bottomLeft1BackgroundTile.initDrawingArea();
      sprite.drawOpaqueSprite(-202, -371);
      this.flameLeftBackground.initDrawingArea();
      sprite.drawOpaqueSprite(-202, -171);
      this.bottomRightImageProducer.initDrawingArea();
      sprite.drawOpaqueSprite(0, -265);
      this.loginMusicImageProducer.initDrawingArea();
      sprite.drawOpaqueSprite(-562, -265);
      this.middleLeft1BackgroundTile.initDrawingArea();
      sprite.drawOpaqueSprite(-128, -171);
      this.middleRightBackgroundBuffer.initDrawingArea();
      sprite.drawOpaqueSprite(-562, -171);
      int[] values = new int[sprite.spriteWidth];

      for (int loopIndex = 0; loopIndex < sprite.spriteHeight; loopIndex++) {
         for (int loopIndex2 = 0; loopIndex2 < sprite.spriteWidth; loopIndex2++) {
            values[loopIndex2] = sprite.pixels[sprite.spriteWidth - loopIndex2 - 1 + sprite.spriteWidth * loopIndex];
         }

         System.arraycopy(values, 0, sprite.pixels, sprite.spriteWidth * loopIndex, sprite.spriteWidth);
      }

      this.flameRightBackground.initDrawingArea();
      sprite.drawOpaqueSprite(382, 0);
      this.bottomLeft0BackgroundTile.initDrawingArea();
      sprite.drawOpaqueSprite(-255, 0);
      this.topLeft1BackgroundTile.initDrawingArea();
      sprite.drawOpaqueSprite(254, 0);
      this.bottomLeft1BackgroundTile.initDrawingArea();
      sprite.drawOpaqueSprite(180, -371);
      this.flameLeftBackground.initDrawingArea();
      sprite.drawOpaqueSprite(180, -171);
      this.bottomRightImageProducer.initDrawingArea();
      sprite.drawOpaqueSprite(382, -265);
      this.loginMusicImageProducer.initDrawingArea();
      sprite.drawOpaqueSprite(-180, -265);
      this.middleLeft1BackgroundTile.initDrawingArea();
      sprite.drawOpaqueSprite(254, -171);
      this.middleRightBackgroundBuffer.initDrawingArea();
      sprite.drawOpaqueSprite(-180, -171);
      byte customSpriteIndex = 100;
      if (logoStyle == 1) {
         customSpriteIndex = 101;
      }

      Sprite customSprite = customSprites[customSpriteIndex];
      this.topLeft1BackgroundTile.initDrawingArea();
      customSprite.drawSprite(382 - customSprite.spriteWidth / 2 - 128, 18);
      this.loginMusicImageProducer.initDrawingArea();
      customSprites[musicVolumeSetting > 0 ? 43 : 44].drawSprite(163, 198);
      System.gc();
   }
   private void processOnDemandQueue() {
      OnDemandRequest onDemandRequest;
      while ((onDemandRequest = this.onDemandFetcher.getNextCompletedRequest()) != null) {
         if (onDemandRequest.dataType == 6) {
            byte[] buffer = onDemandRequest.buffer;
            if (SoundEffect.effects[onDemandRequest.id] == null) {
               SoundEffect.effects[onDemandRequest.id] = new SoundEffect(buffer);
            }
         }

         if (onDemandRequest.dataType == 0) {
            byte[] buffer2 = onDemandRequest.buffer;
            if (hdModels && !isCacheOnlyModelId(onDemandRequest.id)) {
               buffer2 = GzipDecompressor.decompress(GzipDecompressor.readFile(SignLink.findcachedir() + "hdmod/Models/" + onDemandRequest.id + ".gz"));
            }

            Model.decodeModelHeader(buffer2, onDemandRequest.id);
            if ((this.onDemandFetcher.getModelIndex(onDemandRequest.id) & 98) != 0) {
               this.needDrawTabArea = true;
               if (this.backDialogID != -1) {
                  this.inputTaken = true;
               }
            }
         }

         if (onDemandRequest.dataType == 1 && onDemandRequest.buffer != null) {
            byte[] buffer3 = onDemandRequest.buffer;
            if (hdModels) {
               try {
                  AnimationFrame.load(
                     GzipDecompressor.decompress(GzipDecompressor.readFile(SignLink.findcachedir() + "hdmod/Anims/" + onDemandRequest.id + ".gz")), onDemandRequest.id
                  );
               } catch (Exception exception) {
                  exception.printStackTrace();
               }
            } else if (!hdModels && extendedRevisionEnabled && (onDemandRequest.id > 1043 || use2007Models || extendedModelIds.contains(onDemandRequest.id))) {
               try {
                  AnimationFrame.load(buffer3, onDemandRequest.id);
               } catch (Exception exception2) {
                  exception2.printStackTrace();
               }
            } else {
               AnimationFrame.decodeClassic(buffer3);
            }
         }

         if (onDemandRequest.dataType == 2 && onDemandRequest.id == this.nextSong && onDemandRequest.buffer != null) {
            loadedMusicData = new byte[onDemandRequest.buffer.length];
            AnimationSkeleton.copyBytes(onDemandRequest.buffer, 0, loadedMusicData, 0, loadedMusicData.length);
            musicRequestPending = true;
         }

         if (onDemandRequest.dataType == 3 && this.loadingStage == 1) {
            for (int terrainArchiveIdIndex = 0; terrainArchiveIdIndex < this.terrainRegionData.length; terrainArchiveIdIndex++) {
               if (this.terrainArchiveIds[terrainArchiveIdIndex] == onDemandRequest.id) {
                  this.terrainRegionData[terrainArchiveIdIndex] = onDemandRequest.buffer;
                  if (onDemandRequest.buffer == null) {
                     this.terrainArchiveIds[terrainArchiveIdIndex] = -1;
                  }
                  break;
               }

               if (this.objectArchiveIds[terrainArchiveIdIndex] == onDemandRequest.id) {
                  this.objectRegionData[terrainArchiveIdIndex] = onDemandRequest.buffer;
                  if (onDemandRequest.buffer == null) {
                     this.objectArchiveIds[terrainArchiveIdIndex] = -1;
                  }
                  break;
               }
            }
         }

         if (onDemandRequest.dataType == 93 && this.onDemandFetcher.isLandscapeFile(onDemandRequest.id)) {
            RegionBuilder.requestObjectModels(new Buffer(onDemandRequest.buffer), this.onDemandFetcher);
         }
      }
   }
   private void calcFlamesPosition() {
      for (int loopIndex = 10; loopIndex < 117; loopIndex++) {
         if ((int)(Math.random() * 100.0) < 50) {
            this.flameIntensity[loopIndex + 32512] = 255;
         }
      }

      for (int loopIndex2 = 0; loopIndex2 < 100; loopIndex2++) {
         int scalar = (int)(Math.random() * 124.0) + 2;
         int scalar2 = (int)(Math.random() * 128.0) + 128;
         int flameIntensityIndex = scalar + (scalar2 << 7);
         this.flameIntensity[flameIntensityIndex] = 192;
      }

      for (int loopIndex3 = 1; loopIndex3 < 255; loopIndex3++) {
         for (int loopIndex4 = 1; loopIndex4 < 127; loopIndex4++) {
            int flameIntensityScratchIndex = loopIndex4 + (loopIndex3 << 7);
            this.flameIntensityScratch[flameIntensityScratchIndex] = (this.flameIntensity[flameIntensityScratchIndex - 1] + this.flameIntensity[flameIntensityScratchIndex + 1] + this.flameIntensity[flameIntensityScratchIndex - 128] + this.flameIntensity[flameIntensityScratchIndex + 128]) / 4;
         }
      }

      this.flameNoiseOffset += 128;
      if (this.flameNoiseOffset > this.flameNoise.length) {
         this.flameNoiseOffset = this.flameNoiseOffset - this.flameNoise.length;
         int titleRuneSpriteIndex = (int)(Math.random() * 12.0);
         this.randomizeBackground(this.titleRuneSprites[titleRuneSpriteIndex]);
      }

      for (int loopIndex5 = 1; loopIndex5 < 255; loopIndex5++) {
         for (int loopIndex6 = 1; loopIndex6 < 127; loopIndex6++) {
            int flameIntensityIndex2 = loopIndex6 + (loopIndex5 << 7);
            int scalar3;
            if ((scalar3 = this.flameIntensityScratch[flameIntensityIndex2 + 128] - this.flameNoise[flameIntensityIndex2 + this.flameNoiseOffset & this.flameNoise.length - 1] / 5) < 0) {
               scalar3 = 0;
            }

            this.flameIntensity[flameIntensityIndex2] = scalar3;
         }
      }

      System.arraycopy(this.flameLineOffsets, 1, this.flameLineOffsets, 0, 255);
      this.flameLineOffsets[255] = (int)(Math.sin(gameCycle / 14.0) * 16.0 + Math.sin(gameCycle / 15.0) * 14.0 + Math.sin(gameCycle / 16.0) * 12.0);
      if (this.greenFlameTransition > 0) {
         this.greenFlameTransition -= 4;
      }

      if (this.blueFlameTransition > 0) {
         this.blueFlameTransition -= 4;
      }

      if (this.greenFlameTransition == 0 && this.blueFlameTransition == 0) {
         int scalar4;
         if ((scalar4 = (int)(Math.random() * 2000.0)) == 0) {
            this.greenFlameTransition = 1024;
         }

         if (scalar4 == 1) {
            this.blueFlameTransition = 1024;
         }
      }
   }
   private void resetWidgetAnimation(int widgetIndex2) {
      Widget widget;
      int[] values;
      int scalar = (values = (widget = Widget.widgets[widgetIndex2]).childIds).length;

      int widgetIndex;
      for (int position = 0; position < scalar && (widgetIndex = values[position]) != -1; position++) {
         Widget widget2;
         if ((widget2 = Widget.widgets[widgetIndex]).type == 1) {
            this.resetWidgetAnimation(widget2.id);
         }

         widget2.animationFrame = 0;
         widget2.animationFrameCycle = 0;
      }
   }
   private void drawHeadIcon() {
      if (this.hintIconDrawType == 2) {
         this.calcEntityScreenPos((this.hintIconX - this.baseX << 7) + this.hintIconOffsetX, this.hintIconHeight << 1, (this.hintIconY - this.baseY << 7) + this.hintIconOffsetY);
         if (this.spriteDrawX >= 0 && gameCycle % 20 < 10) {
            this.headIconsHint[0].drawSprite(this.spriteDrawX - 12, this.spriteDrawY - 28);
         }
      }
   }
   private static int getBankTabCount() {
      int scalar = 1;

      for (int bankTabIndex = 1; bankTabIndex < bankTabs.length; bankTabIndex++) {
         if (!isBankTabEmpty(bankTabIndex)) {
            scalar++;
         }
      }

      return scalar;
   }
   private boolean isBankTabContainerWidget(int widgetId) {
      for (int bankTabIndex = 0; bankTabIndex < bankTabs.length; bankTabIndex++) {
         if (bankTabs[bankTabIndex].getContainerWidgetId() == widgetId) {
            return true;
         }
      }

      return false;
   }

   private int getHoveredBankTabActionIndex() {
      for (int menuIndex = this.menuActionCount - 1; menuIndex >= 0; menuIndex--) {
         int widgetId = this.menuParam1[menuIndex];
         for (int bankTabIndex = 0; bankTabIndex < bankTabs.length; bankTabIndex++) {
            if (bankTabs[bankTabIndex].getActionWidgetId() == widgetId) {
               return bankTabIndex;
            }
         }
      }

      return -1;
   }

   private void mainGameProcessor() {
      if (screenMode != 0 && (clientWidth != super.getSize().getWidth() || clientHeight != super.getSize().getHeight())) {
         clientWidth = (int)super.getSize().getWidth();
         clientHeight = (int)super.getSize().getHeight();
         if (clientWidth < minimumWindowWidth) {
            clientWidth = minimumWindowWidth;
         }

         if (clientHeight < minimumWindowHeight) {
            clientHeight = minimumWindowHeight;
         }

         this.rebuildViewportBuffers();
      }

      if (this.systemUpdateTime > 1) {
         this.systemUpdateTime--;
      }

      if (this.logoutTimer > 0) {
         this.logoutTimer--;
      }

      int inventoryIdIndex = 0;

      while (inventoryIdIndex < 5 && this.parsePacket()) {
         inventoryIdIndex++;
      }

      if (loggedIn) {
         synchronized (this.mouseRecorder.lock) {
            processMouseMovementSamples: {
               if (flagged) {
                  if (super.clickButton == 0 && this.mouseRecorder.sampleCount < 40) {
                     break processMouseMovementSamples;
                  }

                  this.outgoingBuffer.writeOpcode(45);
                  this.outgoingBuffer.writeByte(0);
                  int currentPosition = this.outgoingBuffer.currentPosition;
                  inventoryIdIndex = 0;

                  for (int mouseYIndex = 0; mouseYIndex < this.mouseRecorder.sampleCount && currentPosition - this.outgoingBuffer.currentPosition < 240; mouseYIndex++) {
                     inventoryIdIndex++;
                     int sourceLastSentMouseY;
                     if ((sourceLastSentMouseY = this.mouseRecorder.mouseY[mouseYIndex]) < 0) {
                        sourceLastSentMouseY = 0;
                     } else if (sourceLastSentMouseY > 502) {
                        sourceLastSentMouseY = 502;
                     }

                     int mouseRecorder2;
                     if ((mouseRecorder2 = this.mouseRecorder.mouseX[mouseYIndex]) < 0) {
                        mouseRecorder2 = 0;
                     } else if (mouseRecorder2 > 764) {
                        mouseRecorder2 = 764;
                     }

                     int scalar = sourceLastSentMouseY * 765 + mouseRecorder2;
                     if (this.mouseRecorder.mouseY[mouseYIndex] == -1 && this.mouseRecorder.mouseX[mouseYIndex] == -1) {
                        mouseRecorder2 = -1;
                        sourceLastSentMouseY = -1;
                        scalar = 524287;
                     }

                     if (mouseRecorder2 != this.lastSentMouseX || sourceLastSentMouseY != this.lastSentMouseY) {
                        int scalar2 = mouseRecorder2 - this.lastSentMouseX;
                        this.lastSentMouseX = mouseRecorder2;
                        int scalar3 = sourceLastSentMouseY - this.lastSentMouseY;
                        this.lastSentMouseY = sourceLastSentMouseY;
                        if (this.duplicateClickCount < 8 && scalar2 >= -32 && scalar2 <= 31 && scalar3 >= -32 && scalar3 <= 31) {
                           scalar2 += 32;
                           scalar3 += 32;
                           this.outgoingBuffer.writeShort((this.duplicateClickCount << 12) + (scalar2 << 6) + scalar3);
                           this.duplicateClickCount = 0;
                        } else if (this.duplicateClickCount < 8) {
                           this.outgoingBuffer.writeMedium(8388608 + (this.duplicateClickCount << 19) + scalar);
                           this.duplicateClickCount = 0;
                        } else {
                           this.outgoingBuffer.writeInt(-1073741824 + (this.duplicateClickCount << 19) + scalar);
                           this.duplicateClickCount = 0;
                        }
                     } else if (this.duplicateClickCount < 2047) {
                        this.duplicateClickCount++;
                     }
                  }

                  this.outgoingBuffer.writeLengthByte(this.outgoingBuffer.currentPosition - currentPosition);
                  if (inventoryIdIndex < this.mouseRecorder.sampleCount) {
                     this.mouseRecorder.sampleCount -= inventoryIdIndex;
                     int mouseXIndex = 0;

                     while (true) {
                        if (mouseXIndex >= this.mouseRecorder.sampleCount) {
                           break processMouseMovementSamples;
                        }

                        this.mouseRecorder.mouseX[mouseXIndex] = this.mouseRecorder.mouseX[mouseXIndex + inventoryIdIndex];
                        this.mouseRecorder.mouseY[mouseXIndex] = this.mouseRecorder.mouseY[mouseXIndex + inventoryIdIndex];
                        mouseXIndex++;
                     }
                  }
               }

               this.mouseRecorder.sampleCount = 0;
            }
         }

         if (super.clickButton != 0) {
            long longScalar;
            if ((longScalar = (super.clickTime - this.lastClickTime) / 50L) > 4095L) {
               longScalar = 4095L;
            }

            this.lastClickTime = super.clickTime;
            inventoryIdIndex = super.clickY;
            if (super.clickY < 0) {
               inventoryIdIndex = 0;
            } else if (inventoryIdIndex > 502) {
               inventoryIdIndex = 502;
            }

            int clickX = super.clickX;
            if (super.clickX < 0) {
               clickX = 0;
            } else if (clickX > 764) {
               clickX = 764;
            }

            int scalar4 = inventoryIdIndex * 765 + clickX;
            byte byteCode = 0;
            if (super.clickButton == 2) {
               byteCode = 1;
            }

            int scalar5 = (int)longScalar;
            this.outgoingBuffer.writeOpcode(241);
            this.outgoingBuffer.writeInt((scalar5 << 20) + (byteCode << 19) + scalar4);
         }

         if (this.cameraPacketCooldown > 0) {
            this.cameraPacketCooldown--;
         }

         if (super.keyStatus[1] == 1 || super.keyStatus[2] == 1 || super.keyStatus[3] == 1 || super.keyStatus[4] == 1) {
            this.cameraPacketPending = true;
         }

         if (this.cameraPacketPending && this.cameraPacketCooldown <= 0) {
            this.cameraPacketCooldown = 20;
            this.cameraPacketPending = false;
            this.outgoingBuffer.writeOpcode(86);
            this.outgoingBuffer.writeShort(this.cameraPitch);
            this.outgoingBuffer.writeShortAdded(this.minimapInt1);
         }

         if (super.hasFocus && !this.focusReported) {
            this.focusReported = true;
            this.outgoingBuffer.writeOpcode(3);
            this.outgoingBuffer.writeByte(1);
         }

         if (!super.hasFocus && this.focusReported) {
            this.focusReported = false;
            this.outgoingBuffer.writeOpcode(3);
            this.outgoingBuffer.writeByte(0);
         }

         Client client = this;
         if (this.loadingStage == 1) {
            Client sourceClient = client;
            int terrainRegionDataIndex = 0;

            byte byteCode2;
            while (true) {
               if (terrainRegionDataIndex >= sourceClient.terrainRegionData.length) {
                  boolean flag = true;

                  for (int objectRegionDataIndex = 0; objectRegionDataIndex < sourceClient.terrainRegionData.length; objectRegionDataIndex++) {
                     byte[] localObjectRegionData;
                     if ((localObjectRegionData = sourceClient.objectRegionData[objectRegionDataIndex]) != null) {
                        int scalar6 = (sourceClient.regionIds[objectRegionDataIndex] >> 8 << 6) - sourceClient.baseX;
                        int scalar7 = ((sourceClient.regionIds[objectRegionDataIndex] & 0xFF) << 6) - sourceClient.baseY;
                        if (sourceClient.constructedViewport) {
                           scalar6 = 10;
                           scalar7 = 10;
                        }

                        flag &= RegionBuilder.areObjectModelsReady(scalar6, localObjectRegionData, scalar7);
                     }
                  }

                  if (!flag) {
                     byteCode2 = -3;
                  } else if (sourceClient.validLocalMap) {
                     byteCode2 = -4;
                  } else {
                     sourceClient.loadingStage = 2;
                     RegionBuilder.buildPlane = sourceClient.plane;
                     sourceClient.rebuildWorldRegion();
                     sourceClient.outgoingBuffer.writeOpcode(121);
                     byteCode2 = 0;
                  }
                  break;
               }

               if (sourceClient.terrainRegionData[terrainRegionDataIndex] == null && sourceClient.terrainArchiveIds[terrainRegionDataIndex] != -1) {
                  byteCode2 = -1;
                  break;
               }

               if (sourceClient.objectRegionData[terrainRegionDataIndex] == null && sourceClient.objectArchiveIds[terrainRegionDataIndex] != -1) {
                  byteCode2 = -2;
                  break;
               }

               terrainRegionDataIndex++;
            }

            byte byteCode3 = byteCode2;
            if (byteCode2 != 0 && System.currentTimeMillis() - client.lastRegionLoadActivityMillis > 360000L) {
               SignLink.reporterror(
                  username
                     + " glcfb "
                     + client.serverSeed
                     + ","
                     + byteCode3
                     + ",false"
                     + ","
                     + client.cacheStores[0]
                     + ","
                     + client.onDemandFetcher.getNodeCount()
                     + ","
                     + client.plane
                     + ","
                     + client.mapRegionX
                     + ","
                     + client.mapRegionY
               );
               client.lastRegionLoadActivityMillis = System.currentTimeMillis();
            }
         }

         if (client.loadingStage == 2 && client.plane != client.lastRenderedPlane) {
            client.lastRenderedPlane = client.plane;
            client.refreshMinimap(client.plane);
         }

         this.processSpawnedObjects();
         this.processAudioQueue();
         this.timeoutCounter++;
         if (this.timeoutCounter > 750) {
            this.dropClient();
         }

         this.updatePlayerInstances();
         this.updateNpcInstances();
         this.updateSpokenTextCycles();
         this.animationCycleDelta++;
         if (this.crossType != 0) {
            this.crossIndex += 20;
            if (this.crossIndex >= 400) {
               this.crossType = 0;
            }
         }

         if (this.atInventoryInterfaceType != 0) {
            this.atInventoryLoopCycle++;
            if (this.atInventoryLoopCycle >= 15) {
               if (this.atInventoryInterfaceType == 2) {
                  this.needDrawTabArea = true;
               }

               if (this.atInventoryInterfaceType == 3) {
                  this.inputTaken = true;
               }

               this.atInventoryInterfaceType = 0;
            }
         }

         if (this.activeInterfaceType != 0) {
            this.widgetDragDuration++;
            if (super.mouseX > this.dragStartX + 5
               || super.mouseX < this.dragStartX - 5
               || super.mouseY > this.dragStartY + 5
               || super.mouseY < this.dragStartY - 5) {
               this.widgetDragThresholdExceeded = true;
            }

            if (super.mouseButtonDown == 0) {
               if (this.activeInterfaceType == 2) {
                  this.needDrawTabArea = true;
               }

               if (this.activeInterfaceType == 3) {
                  this.inputTaken = true;
               }

               this.activeInterfaceType = 0;
               if (this.widgetDragThresholdExceeded && this.widgetDragDuration >= 5) {
                  this.lastActiveInvInterface = -1;
                  this.processRightClick();
                  int bankTabDropIndex = this.getHoveredBankTabActionIndex();
                  if (this.draggedWidgetId != -1
                     && this.isBankTabContainerWidget(this.draggedWidgetId)
                     && bankTabDropIndex >= 0
                     && bankTabDropIndex <= getBankTabCount()) {
                     this.outgoingBuffer.writeOpcode(214);
                     this.outgoingBuffer.writeShortLittleEndianAdded(this.draggedWidgetId);
                     this.outgoingBuffer.writeByteNegated(0);
                     this.outgoingBuffer.writeShortLittleEndianAdded(this.draggedSlot);
                     this.outgoingBuffer.writeShortLittleEndian(bankTabDropIndex);
                     this.outgoingBuffer.writeShortLittleEndianAdded(bankTabSummaryWidgetId);
                  } else if (this.lastActiveInvInterface != -1 && this.draggedWidgetId != -1) {
                     int mouseInvInterfaceIndex = this.mouseInvInterfaceIndex;
                     boolean localFlag = false;
                     if (fetchMusic) {
                        for (int bankTabIndex = 0; bankTabIndex < bankTabs.length; bankTabIndex++) {
                           BankTab bankTab = bankTabs[bankTabIndex];
                           if (this.lastActiveInvInterface == bankTab.containerWidgetId) {
                              if (bankTab.searchResultSlots.size() != 0) {
                                 if (this.mouseInvInterfaceIndex < bankTab.searchResultSlots.size()) {
                                    this.mouseInvInterfaceIndex = (Integer)bankTab.searchResultSlots.get(this.mouseInvInterfaceIndex);
                                 } else {
                                    this.mouseInvInterfaceIndex = bankTab.itemCount;
                                 }
                              }

                              localFlag = true;
                           }

                           if (this.draggedWidgetId == bankTab.containerWidgetId) {
                              if (bankTab.searchResultSlots.size() != 0 && this.draggedSlot < bankTab.searchResultSlots.size()) {
                                 this.draggedSlot = (Integer)bankTab.searchResultSlots.get(this.draggedSlot);
                              }

                              localFlag = true;
                           }
                        }
                     }

                     if (this.lastActiveInvInterface == bankTabSummaryWidgetId || this.draggedWidgetId == bankTabSummaryWidgetId) {
                        if (mouseInvInterfaceIndex < getBankTabCount() + 1) {
                           this.outgoingBuffer.writeOpcode(214);
                           this.outgoingBuffer.writeShortLittleEndianAdded(this.draggedWidgetId);
                           this.outgoingBuffer.writeByteNegated(0);
                           this.outgoingBuffer.writeShortLittleEndianAdded(this.draggedSlot);
                           this.outgoingBuffer.writeShortLittleEndian(mouseInvInterfaceIndex);
                           this.outgoingBuffer.writeShortLittleEndianAdded(this.lastActiveInvInterface);
                        }
                     } else if (this.mouseInvInterfaceIndex != this.draggedSlot || this.lastActiveInvInterface != this.draggedWidgetId) {
                        byte byteCode4 = 0;
                        if (this.lastActiveInvInterface == this.draggedWidgetId && this.mouseInvInterfaceIndex != this.draggedSlot && (!localFlag || !fetchMusic)) {
                           Widget widget = Widget.widgets[this.draggedWidgetId];
                           if (this.interfaceContent206Mode == 1 && widget.contentType == 206) {
                              byteCode4 = 1;
                           }

                           if (widget.inventoryIds[this.mouseInvInterfaceIndex] <= 0) {
                              byteCode4 = 0;
                           }

                           if (widget.replaceItems) {
                              inventoryIdIndex = this.draggedSlot;
                              int mouseInvInterfaceIndex2 = this.mouseInvInterfaceIndex;
                              widget.inventoryIds[mouseInvInterfaceIndex2] = widget.inventoryIds[inventoryIdIndex];
                              widget.inventoryAmounts[mouseInvInterfaceIndex2] = widget.inventoryAmounts[inventoryIdIndex];
                              widget.inventoryIds[inventoryIdIndex] = -1;
                              widget.inventoryAmounts[inventoryIdIndex] = 0;
                           } else if (byteCode4 == 1) {
                              inventoryIdIndex = this.draggedSlot;
                              int mouseInvInterfaceIndex3 = this.mouseInvInterfaceIndex;

                              while (inventoryIdIndex != mouseInvInterfaceIndex3) {
                                 if (inventoryIdIndex > mouseInvInterfaceIndex3) {
                                    widget.swapInventoryItems(inventoryIdIndex, inventoryIdIndex - 1);
                                    inventoryIdIndex--;
                                 } else if (inventoryIdIndex < mouseInvInterfaceIndex3) {
                                    widget.swapInventoryItems(inventoryIdIndex, inventoryIdIndex + 1);
                                    inventoryIdIndex++;
                                 }
                              }
                           } else {
                              widget.swapInventoryItems(this.draggedSlot, this.mouseInvInterfaceIndex);
                           }
                        }

                        this.outgoingBuffer.writeOpcode(214);
                        this.outgoingBuffer.writeShortLittleEndianAdded(this.draggedWidgetId);
                        this.outgoingBuffer.writeByteNegated(byteCode4);
                        this.outgoingBuffer.writeShortLittleEndianAdded(this.draggedSlot);
                        this.outgoingBuffer.writeShortLittleEndian(this.mouseInvInterfaceIndex);
                        this.outgoingBuffer.writeShortLittleEndianAdded(this.lastActiveInvInterface);
                     }
                  }
               } else if ((this.oneButtonMouse == 1 || this.isAddFriendMenuAction(this.menuActionCount - 1)) && this.menuActionCount > 2) {
                  this.determineMenuSize();
               } else if (this.menuActionCount > 0) {
                  int menuActionIdIndex;
                  if (priorityMenuActionIndex != -1) {
                     menuActionIdIndex = priorityMenuActionIndex;
                  } else {
                     menuActionIdIndex = this.menuActionCount - 1;
                  }

                  this.processMenuActions(menuActionIdIndex);
               }

               this.atInventoryLoopCycle = 10;
               super.clickButton = 0;
            }
         }

         if (SceneGraph.clickedTileX != -1) {
            inventoryIdIndex = SceneGraph.clickedTileX;
            int clickedTileY = SceneGraph.clickedTileY;
            if (this.controlDown && this.shiftDown) {
               this.sendTeleportCommand(this.baseX + inventoryIdIndex, this.baseY + clickedTileY);
               this.crossX = super.clickX;
               this.crossY = super.clickY;
               this.crossType = 1;
               this.crossIndex = 0;
            } else if (this.doWalkTo(0, 0, 0, 0, localPlayer.pathY[0], 0, 0, clickedTileY, localPlayer.pathX[0], true, inventoryIdIndex)) {
               this.crossX = super.clickX;
               this.crossY = super.clickY;
               this.crossType = 1;
               this.crossIndex = 0;
            }

            SceneGraph.clickedTileX = -1;
         }

         if (super.clickButton == 1 && this.clickToContinueString != null) {
            this.clickToContinueString = null;
            this.inputTaken = true;
            super.clickButton = 0;
         }

         if (gameframeVersion == 474) {
            Client client2 = this;
            if (super.mouseX >= 5 && client2.mouseX <= 61 && client2.mouseY >= clientHeight - 23 && client2.mouseY <= clientHeight) {
               client2.hoveredChatMode = 0;
               client2.inputTaken = true;
            } else if (client2.mouseX >= 71 && client2.mouseX <= 127 && client2.mouseY >= clientHeight - 23 && client2.mouseY <= clientHeight) {
               client2.hoveredChatMode = 1;
               client2.inputTaken = true;
            } else if (client2.mouseX >= 137 && client2.mouseX <= 193 && client2.mouseY >= clientHeight - 23 && client2.mouseY <= clientHeight) {
               client2.hoveredChatMode = 2;
               client2.inputTaken = true;
            } else if (client2.mouseX >= 203 && client2.mouseX <= 259 && client2.mouseY >= clientHeight - 23 && client2.mouseY <= clientHeight) {
               client2.hoveredChatMode = 3;
               client2.inputTaken = true;
            } else if (client2.mouseX >= 335 && client2.mouseX <= 391 && client2.mouseY >= clientHeight - 23 && client2.mouseY <= clientHeight) {
               client2.hoveredChatMode = 5;
               client2.inputTaken = true;
            } else if (client2.mouseX >= 404 && client2.mouseX <= 515 && client2.mouseY >= clientHeight - 23 && client2.mouseY <= clientHeight) {
               client2.hoveredChatMode = 6;
               client2.inputTaken = true;
            } else {
               client2.hoveredChatMode = -1;
               client2.inputTaken = true;
            }
         }

         if (!this.processMenuClick()) {
            if (this.suppressNextMinimapClick) {
               this.suppressNextMinimapClick = false;
            } else {
               Client client3 = this;
               if (this.minimapState == 0 && client3.fullscreenInterfaceId == -1 && client3.clickButton == 1) {
                  int localClickY = 550;
                  if (gameframeVersion == 474) {
                     localClickY = 545;
                  }

                  int clickX2 = client3.clickX - 25 - localClickY;
                  localClickY = client3.clickY - 5 - 4;
                  if (screenMode != 0) {
                     clickX2 = client3.clickX - (clientWidth - 178);
                     localClickY = client3.clickY - 17;
                  }

                  if (clickX2 >= 0 && localClickY >= 0 && clickX2 < 146 && localClickY < 151) {
                     clickX2 -= 73;
                     localClickY -= 75;
                     int sINEIndex = client3.minimapInt1 + client3.minimapInt2 & 2047;
                     int sINEEntry = Rasterizer3D.SINE[sINEIndex];
                     int cOSINEEntry = Rasterizer3D.COSINE[sINEIndex];
                     sINEEntry = sINEEntry * (client3.minimapInt3 + 256) >> 8;
                     cOSINEEntry = cOSINEEntry * (client3.minimapInt3 + 256) >> 8;
                     int scalar8 = localClickY * sINEEntry + clickX2 * cOSINEEntry >> 11;
                     sINEIndex = localClickY * cOSINEEntry - clickX2 * sINEEntry >> 11;
                     sINEEntry = localPlayer.worldX + scalar8 >> 7;
                     sINEIndex = localPlayer.worldY - sINEIndex >> 7;
                     if (client3.controlDown && client3.shiftDown) {
                        client3.sendTeleportCommand(client3.baseX + sINEEntry, client3.baseY + sINEIndex);
                     } else if (client3.doWalkTo(1, 0, 0, 0, localPlayer.pathY[0], 0, 0, sINEIndex, localPlayer.pathX[0], true, sINEEntry)) {
                        client3.outgoingBuffer.writeByte(clickX2);
                        client3.outgoingBuffer.writeByte(localClickY);
                        client3.outgoingBuffer.writeShort(client3.minimapInt1);
                        client3.outgoingBuffer.writeByte(57);
                        client3.outgoingBuffer.writeByte(client3.minimapInt2);
                        client3.outgoingBuffer.writeByte(client3.minimapInt3);
                        client3.outgoingBuffer.writeByte(89);
                        client3.outgoingBuffer.writeShort(localPlayer.worldX);
                        client3.outgoingBuffer.writeShort(localPlayer.worldY);
                        client3.outgoingBuffer.writeByte(client3.alternativeRouteUsed);
                        client3.outgoingBuffer.writeByte(63);
                     }
                  }

                  if (++minimapClickNoiseCounter > 1151) {
                     minimapClickNoiseCounter = 0;
                     client3.outgoingBuffer.writeOpcode(246);
                     client3.outgoingBuffer.writeByte(0);
                     int outgoingBuffer2 = client3.outgoingBuffer.currentPosition;
                     if ((int)(Math.random() * 2.0) == 0) {
                        client3.outgoingBuffer.writeByte(101);
                     }

                     client3.outgoingBuffer.writeByte(197);
                     client3.outgoingBuffer.writeShort((int)(Math.random() * 65536.0));
                     client3.outgoingBuffer.writeByte((int)(Math.random() * 256.0));
                     client3.outgoingBuffer.writeByte(67);
                     client3.outgoingBuffer.writeShort(14214);
                     if ((int)(Math.random() * 2.0) == 0) {
                        client3.outgoingBuffer.writeShort(29487);
                     }

                     client3.outgoingBuffer.writeShort((int)(Math.random() * 65536.0));
                     if ((int)(Math.random() * 2.0) == 0) {
                        client3.outgoingBuffer.writeByte(220);
                     }

                     client3.outgoingBuffer.writeByte(180);
                     client3.outgoingBuffer.writeLengthByte(client3.outgoingBuffer.currentPosition - outgoingBuffer2);
                  }
               }

               if (screenMode == 0 || screenMode != 0 && osrsResizableFrame) {
                  Client client4 = this;
                  if (this.fullscreenInterfaceId == -1 && client4.clickButton == 1) {
                     if (gameframeVersion != 474) {
                        if (client4.clickX >= 539 && client4.clickX <= 573 && client4.clickY >= 169 && client4.clickY < 205 && client4.tabInterfaceIds[0] != -1) {
                           client4.needDrawTabArea = true;
                           client4.currentTab = 0;
                           client4.tabAreaAltered = true;
                        }

                        if (client4.clickX >= 569 && client4.clickX <= 599 && client4.clickY >= 168 && client4.clickY < 205 && client4.tabInterfaceIds[1] != -1) {
                           client4.needDrawTabArea = true;
                           client4.currentTab = 1;
                           client4.tabAreaAltered = true;
                        }

                        if (client4.clickX >= 597 && client4.clickX <= 627 && client4.clickY >= 168 && client4.clickY < 205 && client4.tabInterfaceIds[2] != -1) {
                           client4.needDrawTabArea = true;
                           client4.currentTab = 2;
                           client4.tabAreaAltered = true;
                        }

                        if (client4.clickX >= 625 && client4.clickX <= 669 && client4.clickY >= 168 && client4.clickY < 203 && client4.tabInterfaceIds[3] != -1) {
                           client4.needDrawTabArea = true;
                           client4.currentTab = 3;
                           client4.tabAreaAltered = true;
                        }

                        if (client4.clickX >= 666 && client4.clickX <= 696 && client4.clickY >= 168 && client4.clickY < 205 && client4.tabInterfaceIds[4] != -1) {
                           client4.needDrawTabArea = true;
                           client4.currentTab = 4;
                           client4.tabAreaAltered = true;
                        }

                        if (client4.clickX >= 694 && client4.clickX <= 724 && client4.clickY >= 168 && client4.clickY < 205 && client4.tabInterfaceIds[5] != -1) {
                           client4.needDrawTabArea = true;
                           client4.currentTab = 5;
                           client4.tabAreaAltered = true;
                        }

                        if (client4.clickX >= 722 && client4.clickX <= 756 && client4.clickY >= 169 && client4.clickY < 205 && client4.tabInterfaceIds[6] != -1) {
                           client4.needDrawTabArea = true;
                           client4.currentTab = 6;
                           client4.tabAreaAltered = true;
                        }

                        if (client4.clickX >= 540 && client4.clickX <= 574 && client4.clickY >= 466 && client4.clickY < 502 && client4.tabInterfaceIds[7] != -1) {
                           client4.needDrawTabArea = true;
                           client4.currentTab = 7;
                           client4.tabAreaAltered = true;
                        }

                        if (client4.clickX >= 572 && client4.clickX <= 602 && client4.clickY >= 466 && client4.clickY < 503 && client4.tabInterfaceIds[8] != -1) {
                           client4.needDrawTabArea = true;
                           client4.currentTab = 8;
                           client4.tabAreaAltered = true;
                        }

                        if (client4.clickX >= 599 && client4.clickX <= 629 && client4.clickY >= 466 && client4.clickY < 503 && client4.tabInterfaceIds[9] != -1) {
                           client4.needDrawTabArea = true;
                           client4.currentTab = 9;
                           client4.tabAreaAltered = true;
                        }

                        if (client4.clickX >= 627 && client4.clickX <= 671 && client4.clickY >= 467 && client4.clickY < 502 && client4.tabInterfaceIds[10] != -1) {
                           client4.needDrawTabArea = true;
                           client4.currentTab = 10;
                           client4.tabAreaAltered = true;
                        }

                        if (client4.clickX >= 669 && client4.clickX <= 699 && client4.clickY >= 466 && client4.clickY < 503 && client4.tabInterfaceIds[11] != -1) {
                           client4.needDrawTabArea = true;
                           client4.currentTab = 11;
                           client4.tabAreaAltered = true;
                        }

                        if (client4.clickX >= 696 && client4.clickX <= 726 && client4.clickY >= 466 && client4.clickY < 503 && client4.tabInterfaceIds[12] != -1) {
                           client4.needDrawTabArea = true;
                           client4.currentTab = 12;
                           client4.tabAreaAltered = true;
                        }

                        if (client4.clickX >= 724 && client4.clickX <= 758 && client4.clickY >= 466 && client4.clickY < 502 && client4.tabInterfaceIds[13] != -1) {
                           client4.needDrawTabArea = true;
                           client4.currentTab = 13;
                           client4.tabAreaAltered = true;
                        }
                     } else {
                        int localClientWidth = 0;
                        int localClientHeight = 0;
                        if (screenMode != 0 && osrsResizableFrame) {
                           localClientWidth = clientWidth - 758;
                           localClientHeight = clientHeight - 499;
                        }

                        if (client4.clickX >= localClientWidth + 522
                           && client4.clickX <= localClientWidth + 559
                           && client4.clickY >= localClientHeight + 168
                           && client4.clickY < localClientHeight + 203
                           && client4.tabInterfaceIds[0] != -1) {
                           client4.needDrawTabArea = true;
                           client4.currentTab = 0;
                           client4.tabAreaAltered = true;
                        }

                        if (client4.clickX >= localClientWidth + 560
                           && client4.clickX <= localClientWidth + 592
                           && client4.clickY >= localClientHeight + 168
                           && client4.clickY < localClientHeight + 203
                           && client4.tabInterfaceIds[1] != -1) {
                           client4.needDrawTabArea = true;
                           client4.currentTab = 1;
                           client4.tabAreaAltered = true;
                        }

                        if (client4.clickX >= localClientWidth + 593
                           && client4.clickX <= localClientWidth + 625
                           && client4.clickY >= localClientHeight + 168
                           && client4.clickY < localClientHeight + 203
                           && client4.tabInterfaceIds[2] != -1) {
                           client4.needDrawTabArea = true;
                           client4.currentTab = 2;
                           client4.tabAreaAltered = true;
                        }

                        if (client4.clickX >= localClientWidth + 626
                           && client4.clickX <= localClientWidth + 658
                           && client4.clickY >= localClientHeight + 168
                           && client4.clickY < localClientHeight + 203
                           && client4.tabInterfaceIds[3] != -1) {
                           client4.needDrawTabArea = true;
                           client4.currentTab = 3;
                           client4.tabAreaAltered = true;
                        }

                        if (client4.clickX >= localClientWidth + 659
                           && client4.clickX <= localClientWidth + 691
                           && client4.clickY >= localClientHeight + 168
                           && client4.clickY < localClientHeight + 203
                           && client4.tabInterfaceIds[4] != -1) {
                           client4.needDrawTabArea = true;
                           client4.currentTab = 4;
                           client4.tabAreaAltered = true;
                        }

                        if (client4.clickX >= localClientWidth + 692
                           && client4.clickX <= localClientWidth + 724
                           && client4.clickY >= localClientHeight + 168
                           && client4.clickY < localClientHeight + 203
                           && client4.tabInterfaceIds[5] != -1) {
                           client4.needDrawTabArea = true;
                           client4.currentTab = 5;
                           client4.tabAreaAltered = true;
                        }

                        if (client4.clickX >= localClientWidth + 725
                           && client4.clickX <= localClientWidth + 762
                           && client4.clickY >= localClientHeight + 168
                           && client4.clickY < localClientHeight + 203
                           && client4.tabInterfaceIds[6] != -1) {
                           client4.needDrawTabArea = true;
                           client4.currentTab = 6;
                           client4.tabAreaAltered = true;
                        }

                        if (client4.clickX >= localClientWidth + 522
                           && client4.clickX <= localClientWidth + 559
                           && client4.clickY >= localClientHeight + 466
                           && client4.clickY < localClientHeight + 503
                           && client4.tabInterfaceIds[7] != -1) {
                           client4.needDrawTabArea = true;
                           client4.currentTab = 7;
                           client4.tabAreaAltered = true;
                        }

                        if (client4.clickX >= localClientWidth + 560
                           && client4.clickX <= localClientWidth + 592
                           && client4.clickY >= localClientHeight + 466
                           && client4.clickY < localClientHeight + 503
                           && client4.tabInterfaceIds[8] != -1) {
                           client4.needDrawTabArea = true;
                           client4.currentTab = 8;
                           client4.tabAreaAltered = true;
                        }

                        if (client4.clickX >= localClientWidth + 593
                           && client4.clickX <= localClientWidth + 625
                           && client4.clickY >= localClientHeight + 466
                           && client4.clickY < localClientHeight + 503
                           && client4.tabInterfaceIds[9] != -1) {
                           client4.needDrawTabArea = true;
                           client4.currentTab = 9;
                           client4.tabAreaAltered = true;
                        }

                        if (client4.clickX >= localClientWidth + 626
                           && client4.clickX <= localClientWidth + 658
                           && client4.clickY >= localClientHeight + 466
                           && client4.clickY < localClientHeight + 503
                           && client4.tabInterfaceIds[10] != -1) {
                           client4.needDrawTabArea = true;
                           client4.currentTab = 10;
                           client4.tabAreaAltered = true;
                        }

                        if (client4.clickX >= localClientWidth + 659
                           && client4.clickX <= localClientWidth + 691
                           && client4.clickY >= localClientHeight + 466
                           && client4.clickY < localClientHeight + 503
                           && client4.tabInterfaceIds[11] != -1) {
                           client4.needDrawTabArea = true;
                           client4.currentTab = 11;
                           client4.tabAreaAltered = true;
                        }

                        if (client4.clickX >= localClientWidth + 692
                           && client4.clickX <= localClientWidth + 724
                           && client4.clickY >= localClientHeight + 466
                           && client4.clickY < localClientHeight + 503
                           && client4.tabInterfaceIds[12] != -1) {
                           client4.needDrawTabArea = true;
                           client4.currentTab = 12;
                           client4.tabAreaAltered = true;
                        }

                        if (client4.clickX >= localClientWidth + 725
                           && client4.clickX <= localClientWidth + 762
                           && client4.clickY >= localClientHeight + 466
                           && client4.clickY < localClientHeight + 503
                           && client4.tabInterfaceIds[13] != -1) {
                           client4.needDrawTabArea = true;
                           client4.currentTab = 13;
                           client4.tabAreaAltered = true;
                        }
                     }

                     if (client4.flashingSidebarId == client4.currentTab) {
                        client4.outgoingBuffer.writeOpcode(152);
                        client4.outgoingBuffer.writeByte(client4.currentTab);
                     }
                  }
               } else {
                  this.processTabClick();
               }

               this.processChatModeClick();
            }
         }

         if (super.mouseButtonDown == 1 || super.clickButton == 1) {
            this.scrollbarClickTicks++;
         }

         if (this.chatTooltipWidgetId == 0 && this.tabTooltipWidgetId == 0 && this.viewportTooltipWidgetId == 0) {
            if (this.tooltipHoverTicks > 0) {
               this.tooltipHoverTicks--;
            }
         } else {
            if (this.lastViewportTooltipWidgetId != this.viewportTooltipWidgetId) {
               this.lastViewportTooltipWidgetId = this.viewportTooltipWidgetId;
               this.tooltipHoverTicks = 0;
            }

            if (this.tooltipHoverTicks < this.tooltipDelayTicks && !this.menuOpen) {
               this.tooltipHoverTicks++;
               if (this.tooltipHoverTicks == this.tooltipDelayTicks) {
                  if (this.chatTooltipWidgetId != 0) {
                     this.inputTaken = true;
                  }

                  if (this.tabTooltipWidgetId != 0) {
                     this.needDrawTabArea = true;
                  }
               }
            }
         }

         if (this.loadingStage == 2) {
            this.updateCameraFollow();
         }

         if (this.loadingStage == 2 && this.oriented) {
            this.calculateCameraPosition();
         }

         for (int cameraShakePhaseIndex = 0; cameraShakePhaseIndex < 5; cameraShakePhaseIndex++) {
            this.cameraShakePhase[cameraShakePhaseIndex]++;
         }

         this.manageTextInputs();
         super.idleTime++;
         if (super.idleTime > 4500) {
            this.logoutTimer = 250;
            super.idleTime -= 500;
            this.outgoingBuffer.writeOpcode(202);
         }

         this.cameraJitterCounter++;
         if (this.cameraJitterCounter > 500) {
            this.cameraJitterCounter = 0;
            if (((inventoryIdIndex = (int)(Math.random() * 8.0)) & 1) == 1) {
               this.cameraX = this.cameraX + this.cameraJitterXVelocity;
            }

            if ((inventoryIdIndex & 2) == 2) {
               this.cameraY = this.cameraY + this.cameraYVelocity;
            }

            if ((inventoryIdIndex & 4) == 4) {
               this.cameraRotation = this.cameraRotation + this.cameraRotationVelocity;
            }
         }

         if (this.cameraX < -50) {
            this.cameraJitterXVelocity = 2;
         }

         if (this.cameraX > 50) {
            this.cameraJitterXVelocity = -2;
         }

         if (this.cameraY < -55) {
            this.cameraYVelocity = 2;
         }

         if (this.cameraY > 55) {
            this.cameraYVelocity = -2;
         }

         if (this.cameraRotation < -40) {
            this.cameraRotationVelocity = 1;
         }

         if (this.cameraRotation > 40) {
            this.cameraRotationVelocity = -1;
         }

         this.minimapJitterCounter++;
         if (this.minimapJitterCounter > 500) {
            this.minimapJitterCounter = 0;
            if (((inventoryIdIndex = (int)(Math.random() * 8.0)) & 1) == 1) {
               this.minimapInt2 = this.minimapInt2 + this.minimapZoomVelocity;
            }

            if ((inventoryIdIndex & 2) == 2) {
               this.minimapInt3 = this.minimapInt3 + this.minimapRotationVelocity;
            }
         }

         if (this.minimapInt2 < -60) {
            this.minimapZoomVelocity = 2;
         }

         if (this.minimapInt2 > 60) {
            this.minimapZoomVelocity = -2;
         }

         if (this.minimapInt3 < -20) {
            this.minimapRotationVelocity = 1;
         }

         if (this.minimapInt3 > 10) {
            this.minimapRotationVelocity = -1;
         }

         this.outboundFlushCounter++;
         if (this.outboundFlushCounter > 50) {
            this.outgoingBuffer.writeOpcode(0);
         }

         try {
            if (this.connection != null && this.outgoingBuffer.currentPosition > 0) {
               this.connection.queueBytes(this.outgoingBuffer.currentPosition, this.outgoingBuffer.buffer);
               this.outgoingBuffer.currentPosition = 0;
               this.outboundFlushCounter = 0;
               return;
            }
         } catch (IOException exception) {
            this.dropClient();
            return;
         } catch (Exception exception2) {
            this.resetLogout();
         }
      }
   }
   private void setupLoginScreenBuffers() {
      if (this.topLeft1BackgroundTile == null) {
         super.graphicsBuffer = null;
         this.chatboxImageProducer = null;
         this.minimapImageProducer = null;
         this.tabImageProducer = null;
         this.gameScreenImageProducer = null;
         this.bottomFrameStripBuffer = null;
         this.bottomRightFrameStripBuffer = null;
         this.topRightFrameStripBuffer = null;
         this.rightFrameStripBuffer = null;
         this.middleRightFrameStripBuffer = null;
         this.chatRightFrameStripBuffer = null;
         this.chatTopFrameStripBuffer = null;
         this.chatLeftFrameStripBuffer = null;
         this.minimapRightFrameStripBuffer = null;
         this.minimapLeftFrameStripBuffer = null;
         this.chatSettingImageProducer = null;
         this.getGameComponent();
         this.flameRightBackground = new BufferedImageGraphicsBuffer(128, 265);
         Rasterizer2D.clear();
         this.getGameComponent();
         this.bottomLeft0BackgroundTile = new BufferedImageGraphicsBuffer(128, 265);
         Rasterizer2D.clear();
         this.getGameComponent();
         this.topLeft1BackgroundTile = new BufferedImageGraphicsBuffer(509, 171);
         Rasterizer2D.clear();
         this.getGameComponent();
         this.bottomLeft1BackgroundTile = new BufferedImageGraphicsBuffer(360, 132);
         Rasterizer2D.clear();
         this.getGameComponent();
         this.flameLeftBackground = new BufferedImageGraphicsBuffer(360, 200);
         Rasterizer2D.clear();
         this.getGameComponent();
         this.bottomRightImageProducer = new BufferedImageGraphicsBuffer(202, 238);
         Rasterizer2D.clear();
         this.getGameComponent();
         this.loginMusicImageProducer = new BufferedImageGraphicsBuffer(203, 238);
         Rasterizer2D.clear();
         this.getGameComponent();
         this.middleLeft1BackgroundTile = new BufferedImageGraphicsBuffer(74, 94);
         Rasterizer2D.clear();
         this.getGameComponent();
         this.middleRightBackgroundBuffer = new BufferedImageGraphicsBuffer(75, 94);
         Rasterizer2D.clear();
         if (this.titleArchive != null) {
            this.drawLogo();
            this.loadTitleScreen();
         }

         this.updateClientWindowSize(true);
         this.welcomeScreenRaised = true;
      }
   }
   @Override
   final void drawLoadingText(int newLoadingErrorCode, String text) {
      this.loadingErrorCode = newLoadingErrorCode;
      this.loadingStatusText = text;
      this.setupLoginScreenBuffers();
      if (this.titleArchive == null) {
         super.drawLoadingText(newLoadingErrorCode, text);
      } else {
         this.flameLeftBackground.initDrawingArea();
         this.boldFont.textCenter(16777215, "RuneScape is loading - please wait...", 54, 180);
         Rasterizer2D.drawRectangle(28, 304, 34, 9179409, 62);
         Rasterizer2D.drawRectangle(29, 302, 32, 0, 63);
         Rasterizer2D.fillRectangle(30, 64, 30, 9179409, newLoadingErrorCode * 3);
         Rasterizer2D.fillRectangle(30, 64, 30 + newLoadingErrorCode * 3, 0, 300 - newLoadingErrorCode * 3);
         this.boldFont.textCenter(16777215, text, 85, 180);
         this.flameLeftBackground.drawGraphics(171, super.graphics, 202);
         if (this.welcomeScreenRaised) {
            this.welcomeScreenRaised = false;
            if (!this.flameThreadRunning) {
               this.flameRightBackground.drawGraphics(0, super.graphics, 0);
               this.bottomLeft0BackgroundTile.drawGraphics(0, super.graphics, 637);
            }

            this.topLeft1BackgroundTile.drawGraphics(0, super.graphics, 128);
            this.bottomLeft1BackgroundTile.drawGraphics(371, super.graphics, 202);
            this.bottomRightImageProducer.drawGraphics(265, super.graphics, 0);
            this.loginMusicImageProducer.drawGraphics(265, super.graphics, 562);
            this.middleLeft1BackgroundTile.drawGraphics(171, super.graphics, 128);
            this.middleRightBackgroundBuffer.drawGraphics(171, super.graphics, 562);
         }
      }
   }
   private void handleScrollbar(int width, int height, int mouseX, int mouseY, Widget widget, int scalarArgument, boolean flag, int chatContentHeight) {
      byte byteCode;
      if (this.scrollbarDragging) {
         byteCode = 32;
      } else {
         byteCode = 0;
      }

      this.scrollbarDragging = false;
      if (mouseX >= width && mouseX < width + 16 && mouseY >= scalarArgument && mouseY < scalarArgument + 16) {
         widget.scrollPosition = widget.scrollPosition - (this.scrollbarClickTicks << 2);
         if (flag) {
            this.needDrawTabArea = true;
            return;
         }
      } else if (mouseX >= width && mouseX < width + 16 && mouseY >= scalarArgument + height - 16 && mouseY < scalarArgument + height) {
         widget.scrollPosition = widget.scrollPosition + (this.scrollbarClickTicks << 2);
         if (flag) {
            this.needDrawTabArea = true;
            return;
         }
      } else if (mouseX >= width - byteCode && mouseX < width + 16 + byteCode && mouseY >= scalarArgument + 16 && mouseY < scalarArgument + height - 16 && this.scrollbarClickTicks > 0) {
         if ((width = (height - 32) * height / chatContentHeight) < 8) {
            width = 8;
         }

         mouseX = mouseY - scalarArgument - 16 - width / 2;
         width = height - 32 - width;
         widget.scrollPosition = (chatContentHeight - height) * mouseX / width;
         if (flag) {
            this.needDrawTabArea = true;
         }

         this.scrollbarDragging = true;
      }
   }
   private void requestMusicTrackWithFade(int scalarArgument, int newRequestedMusicVolume, int newNextSong) {
      if (isMidiPlayerAvailable()) {
         this.nextSong = newNextSong;
         this.onDemandFetcher.provide(2, this.nextSong);
         requestedMusicVolume = newRequestedMusicVolume;
         musicTransitionDelay = -1;
         requestedMusicLoop = true;
         musicFadeDuration = 18;
      }
   }
   private boolean clickObject(int newScene, int tileY, int clippingDataIndex) {
      int blockingMask = newScene >> 14 & 32767;
      if ((newScene = this.scene.getArrangement(this.plane, clippingDataIndex, tileY, newScene)) == -1) {
         return false;
      }

      int scalar = newScene & 31;
      newScene = newScene >> 6 & 3;
      if (scalar != 10 && scalar != 11 && scalar != 22) {
         this.doWalkTo(2, newScene, 0, scalar + 1, localPlayer.pathY[0], 0, 0, tileY, localPlayer.pathX[0], false, clippingDataIndex);
      } else {
         ObjectDefinition objectDefinition = ObjectDefinition.lookup(blockingMask);
         int localSizeX;
         if (newScene != 0 && newScene != 2) {
            scalar = objectDefinition.sizeY;
            localSizeX = objectDefinition.sizeX;
         } else {
            scalar = objectDefinition.sizeX;
            localSizeX = objectDefinition.sizeY;
         }

         blockingMask = objectDefinition.blockingMask;
         if (newScene != 0) {
            blockingMask = (blockingMask << newScene & 15) + (blockingMask >> 4 - newScene);
         }

         this.doWalkTo(2, 0, localSizeX, 0, localPlayer.pathY[0], scalar, blockingMask, tileY, localPlayer.pathX[0], false, clippingDataIndex);
      }

      this.crossX = super.clickX;
      this.crossY = super.clickY;
      this.crossType = 2;
      this.crossIndex = 0;
      return true;
   }
   private Archive loadArchive(int id, String text, String newText, int archiveCrc, int scalarArgument) {
      byte[] byteBuffer = null;
      byte position = 5;

      try {
         if (this.cacheStores[0] != null) {
            byteBuffer = this.cacheStores[0].read(id);
         }
      } catch (Exception exception) {
      }

      if (byteBuffer != null) {
         if (archiveCrc == 0) {
            return new Archive(byteBuffer);
         }
         java.util.zip.CRC32 cachedArchiveCrc = new java.util.zip.CRC32();
         cachedArchiveCrc.update(byteBuffer);
         int cachedArchiveChecksum = (int)cachedArchiveCrc.getValue();
         if (cachedArchiveChecksum == archiveCrc) {
            return new Archive(byteBuffer);
         }

         System.err.println(
            "Cache archive CRC mismatch for " + text + ": cached=" + cachedArchiveChecksum + " expected=" + archiveCrc + "; re-downloading."
         );
         byteBuffer = null;
      }

      while (byteBuffer == null) {
         String localText = "Unknown error";
         this.drawLoadingText(scalarArgument, "Requesting " + text);

         try {
            int scalar = 0;
            DataInputStream dataInputStream = this.openJagGrabInputStream(newText + archiveCrc);
            byte[] byteBuffer2 = new byte[6];
            dataInputStream.readFully(byteBuffer2, 0, 6);
            Buffer buffer;
            (buffer = new Buffer(byteBuffer2)).currentPosition = 3;
            int readUnsignedMediumOrLength = buffer.readUnsignedMedium() + 6;
            int scalar2 = 6;
            byteBuffer = new byte[readUnsignedMediumOrLength];
            System.arraycopy(byteBuffer2, 0, byteBuffer, 0, 6);

            while (scalar2 < readUnsignedMediumOrLength) {
               int scalar3;
               if ((scalar3 = readUnsignedMediumOrLength - scalar2) > 1000) {
                  scalar3 = 1000;
               }

               int decodedEntry;
               if ((decodedEntry = dataInputStream.read(byteBuffer, scalar2, scalar3)) < 0) {
                  localText = "Length error: " + scalar2 + "/" + readUnsignedMediumOrLength;
                  throw new IOException("EOF");
               }

               int scalar4;
               if ((scalar4 = (scalar2 += decodedEntry) * 100 / readUnsignedMediumOrLength) != scalar) {
                  this.drawLoadingText(scalarArgument, "Loading " + text + " - " + scalar4 + "%");
               }

               scalar = scalar4;
            }

            dataInputStream.close();

            try {
               if (this.cacheStores[0] != null) {
                  java.util.zip.CRC32 downloadedArchiveCrc = new java.util.zip.CRC32();
                  downloadedArchiveCrc.update(byteBuffer);
                  int downloadedArchiveChecksum = (int)downloadedArchiveCrc.getValue();
                  if (archiveCrc != 0 && downloadedArchiveChecksum != archiveCrc) {
                     System.err.println(
                        "Downloaded archive CRC mismatch for " + text + ": downloaded=" + downloadedArchiveChecksum + " expected=" + archiveCrc + "."
                     );
                     byteBuffer = null;
                  } else {
                     this.cacheStores[0].write(byteBuffer.length, byteBuffer, id);
                  }
               }
            } catch (Exception exception7) {
               this.cacheStores[0] = null;
            }
         } catch (IOException exception2) {
            if (localText.equals("Unknown error")) {
               localText = "Connection error";
            }

            byteBuffer = null;
         } catch (NullPointerException exception3) {
            localText = "Null error";
            byteBuffer = null;
            if (!SignLink.unusedPublicFlag) {
               return null;
            }
         } catch (ArrayIndexOutOfBoundsException exception4) {
            localText = "Bounds error";
            byteBuffer = null;
            if (!SignLink.unusedPublicFlag) {
               return null;
            }
         } catch (Exception exception5) {
            localText = "Unexpected error";
            byteBuffer = null;
            if (!SignLink.unusedPublicFlag) {
               return null;
            }
         }

         if (byteBuffer == null) {
            for (int loopIndex = position; loopIndex > 0; loopIndex--) {
               this.drawLoadingText(scalarArgument, localText + " - Retrying in " + loopIndex);

               try {
                  Thread.sleep(1000L);
               } catch (Exception exception6) {
               }
            }

            if ((position <<= 1) > 60) {
               position = 60;
            }

            this.archiveRetryToggle = !this.archiveRetryToggle;
         }
      }

      return new Archive(byteBuffer);
   }
   private void dropClient() {
      if (this.logoutTimer > 0) {
         this.resetLogout();
      } else if (this.fullscreenInterfaceId > 0) {
         this.fullscreenInterfaceId = -1;
      } else {
         this.gameScreenImageProducer.initDrawingArea();
         this.drawLoadingMessages(2, "Connection lost.", "Please wait - attempting to reestablish.");
         this.gameScreenImageProducer.drawToBuffer(screenMode == 0 ? 4 : 0, this.frameBuffer, screenMode == 0 ? 4 : 0);
         this.minimapState = 0;
         this.destX = 0;
         BufferedConnection bufferedConnection = this.connection;
         loggedIn = false;
         this.loginFailures = 0;
         this.login(username, password, true);
         if (!loggedIn) {
            this.resetLogout();
         }

         try {
            bufferedConnection.close();
         } catch (Exception exception) {
         }
      }
   }
   private void processMenuActions(int menuActionIdIndex) {
      if (menuActionIdIndex >= 0) {
         if (this.inputDialogState != 0 && this.inputDialogState != 3 && (this.openInterfaceId != 5292 || this.inputDialogState != 2)) {
            this.inputDialogState = 0;
            this.inputTaken = true;
         }

         int atInventoryIndexOrMenuParam0 = this.menuParam0[menuActionIdIndex];
         int menuParam1Entry = this.menuParam1[menuActionIdIndex];
         int menuActionId = this.menuActionIds[menuActionIdIndex];
         int menuParam2Entry = this.menuParam2[menuActionIdIndex];
         if (menuActionId >= 2000) {
            menuActionId -= 2000;
         }

         Npc npc;
         if (menuActionId == 582 && (npc = this.npcs[menuParam2Entry]) != null) {
            this.doWalkTo(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, npc.pathY[0], localPlayer.pathX[0], false, npc.pathX[0]);
            this.crossX = super.clickX;
            this.crossY = super.clickY;
            this.crossType = 2;
            this.crossIndex = 0;
            this.outgoingBuffer.writeOpcode(57);
            this.outgoingBuffer.writeShortAdded(this.selectedItemId);
            this.outgoingBuffer.writeShortAdded(menuParam2Entry);
            this.outgoingBuffer.writeShortLittleEndian(this.selectedItemSlot);
            this.outgoingBuffer.writeShortAdded(this.selectedItemWidgetId);
         }

         if (menuActionId == 1050) {
            if (!this.runEnabled) {
               this.runEnabled = true;
               this.outgoingBuffer.writeOpcode(185);
               this.outgoingBuffer.writeShort(153);
            } else {
               this.runEnabled = false;
               this.outgoingBuffer.writeOpcode(185);
               this.outgoingBuffer.writeShort(152);
            }
         }

         if (menuActionId == 234) {
            if (!this.doWalkTo(2, 0, 0, 0, localPlayer.pathY[0], 0, 0, menuParam1Entry, localPlayer.pathX[0], false, atInventoryIndexOrMenuParam0)) {
               this.doWalkTo(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, menuParam1Entry, localPlayer.pathX[0], false, atInventoryIndexOrMenuParam0);
            }

            this.crossX = super.clickX;
            this.crossY = super.clickY;
            this.crossType = 2;
            this.crossIndex = 0;
            this.outgoingBuffer.writeOpcode(236);
            this.outgoingBuffer.writeShortLittleEndian(menuParam1Entry + this.baseY);
            this.outgoingBuffer.writeShort(menuParam2Entry);
            this.outgoingBuffer.writeShortLittleEndian(atInventoryIndexOrMenuParam0 + this.baseX);
         }

         if (menuActionId == 62 && this.clickObject(menuParam2Entry, menuParam1Entry, atInventoryIndexOrMenuParam0)) {
            this.outgoingBuffer.writeOpcode(192);
            this.outgoingBuffer.writeShort(this.selectedItemWidgetId);
            this.outgoingBuffer.writeShortLittleEndian(menuParam2Entry >> 14 & 32767);
            this.outgoingBuffer.writeShortLittleEndianAdded(menuParam1Entry + this.baseY);
            this.outgoingBuffer.writeShortLittleEndian(this.selectedItemSlot);
            this.outgoingBuffer.writeShortLittleEndianAdded(atInventoryIndexOrMenuParam0 + this.baseX);
            this.outgoingBuffer.writeShort(this.selectedItemId);
         }

         if (menuActionId == 511) {
            if (!this.doWalkTo(2, 0, 0, 0, localPlayer.pathY[0], 0, 0, menuParam1Entry, localPlayer.pathX[0], false, atInventoryIndexOrMenuParam0)) {
               this.doWalkTo(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, menuParam1Entry, localPlayer.pathX[0], false, atInventoryIndexOrMenuParam0);
            }

            this.crossX = super.clickX;
            this.crossY = super.clickY;
            this.crossType = 2;
            this.crossIndex = 0;
            this.outgoingBuffer.writeOpcode(25);
            this.outgoingBuffer.writeShortLittleEndian(this.selectedItemWidgetId);
            this.outgoingBuffer.writeShortAdded(this.selectedItemId);
            this.outgoingBuffer.writeShort(menuParam2Entry);
            this.outgoingBuffer.writeShortAdded(menuParam1Entry + this.baseY);
            this.outgoingBuffer.writeShortLittleEndianAdded(this.selectedItemSlot);
            this.outgoingBuffer.writeShort(atInventoryIndexOrMenuParam0 + this.baseX);
         }

         if (menuActionId == 74) {
            this.outgoingBuffer.writeOpcode(122);
            this.outgoingBuffer.writeShortLittleEndianAdded(menuParam1Entry);
            this.outgoingBuffer.writeShortAdded(atInventoryIndexOrMenuParam0);
            this.outgoingBuffer.writeShortLittleEndian(menuParam2Entry);
            this.atInventoryLoopCycle = 0;
            this.atInventoryInterface = menuParam1Entry;
            this.atInventoryIndex = atInventoryIndexOrMenuParam0;
            this.atInventoryInterfaceType = 2;
            if (Widget.widgets[menuParam1Entry].parentId == this.openInterfaceId) {
               this.atInventoryInterfaceType = 1;
            }

            if (Widget.widgets[menuParam1Entry].parentId == this.backDialogID) {
               this.atInventoryInterfaceType = 3;
            }
         }

         if (menuActionId == 222) {
            this.outgoingBuffer.writeOpcode(222);
            this.outgoingBuffer.writeShort(menuParam1Entry);
            this.outgoingBuffer.writeUnsignedByte(this.hoveredMenuActionIndex);
         }

         if (menuActionId == 315) {
            Widget widget = Widget.widgets[menuParam1Entry];
            boolean flag = true;
            if (widget.contentType > 0) {
               flag = this.promptUserForInput(widget);
            }

            if (flag) {
               this.outgoingBuffer.writeOpcode(185);
               this.outgoingBuffer.writeShort(menuParam1Entry);
            }
         }

         Player player;
         if (menuActionId == 561 && (player = this.players[menuParam2Entry]) != null) {
            this.doWalkTo(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, player.pathY[0], localPlayer.pathX[0], false, player.pathX[0]);
            this.crossX = super.clickX;
            this.crossY = super.clickY;
            this.crossType = 2;
            this.crossIndex = 0;
            if ((playerInteractionNoiseCounter += menuParam2Entry) >= 90) {
               this.outgoingBuffer.writeOpcode(136);
               playerInteractionNoiseCounter = 0;
            }

            this.outgoingBuffer.writeOpcode(128);
            this.outgoingBuffer.writeShort(menuParam2Entry);
         }

         if (menuActionId == 20 && (npc = this.npcs[menuParam2Entry]) != null) {
            this.doWalkTo(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, npc.pathY[0], localPlayer.pathX[0], false, npc.pathX[0]);
            this.crossX = super.clickX;
            this.crossY = super.clickY;
            this.crossType = 2;
            this.crossIndex = 0;
            this.outgoingBuffer.writeOpcode(155);
            this.outgoingBuffer.writeShortLittleEndian(menuParam2Entry);
         }

         if (menuActionId == 779 && (player = this.players[menuParam2Entry]) != null) {
            this.doWalkTo(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, player.pathY[0], localPlayer.pathX[0], false, player.pathX[0]);
            this.crossX = super.clickX;
            this.crossY = super.clickY;
            this.crossType = 2;
            this.crossIndex = 0;
            this.outgoingBuffer.writeOpcode(153);
            this.outgoingBuffer.writeShortLittleEndian(menuParam2Entry);
         }

         if (menuActionId == 516) {
            if (!this.menuOpen) {
               SceneGraph.click(super.clickY - 4, super.clickX - 4);
            } else {
               SceneGraph.click(menuParam1Entry - 4, atInventoryIndexOrMenuParam0 - 4);
            }
         }

         if (menuActionId == 1062) {
            if ((objectActionNoiseCounter = objectActionNoiseCounter + this.baseX) >= 113) {
               this.outgoingBuffer.writeOpcode(183);
               this.outgoingBuffer.writeMedium(15086193);
               objectActionNoiseCounter = 0;
            }

            this.clickObject(menuParam2Entry, menuParam1Entry, atInventoryIndexOrMenuParam0);
            this.outgoingBuffer.writeOpcode(228);
            this.outgoingBuffer.writeShortAdded(menuParam2Entry >> 14 & 32767);
            this.outgoingBuffer.writeShortAdded(menuParam1Entry + this.baseY);
            this.outgoingBuffer.writeShort(atInventoryIndexOrMenuParam0 + this.baseX);
         }

         if (menuActionId == 679 && !this.continuedDialogue) {
            this.outgoingBuffer.writeOpcode(40);
            this.outgoingBuffer.writeShort(menuParam1Entry);
            this.continuedDialogue = true;
         }

         if (menuActionId == 431) {
            this.outgoingBuffer.writeOpcode(129);
            int scalar = atInventoryIndexOrMenuParam0;

            for (int bankTabIndex = 0; bankTabIndex < bankTabs.length; bankTabIndex++) {
               BankTab bankTab = bankTabs[bankTabIndex];
               if (menuParam1Entry == bankTab.getContainerWidgetId() && bankTab.getSearchResultSlots().size() != 0) {
                  scalar = (Integer)bankTab.getSearchResultSlots().get(atInventoryIndexOrMenuParam0);
               }
            }

            this.outgoingBuffer.writeShortAdded(scalar);
            this.outgoingBuffer.writeShort(menuParam1Entry);
            this.outgoingBuffer.writeShortAdded(menuParam2Entry);
            this.atInventoryLoopCycle = 0;
            this.atInventoryInterface = menuParam1Entry;
            this.atInventoryIndex = atInventoryIndexOrMenuParam0;
            this.atInventoryInterfaceType = 2;
            if (Widget.widgets[menuParam1Entry].parentId == this.openInterfaceId) {
               this.atInventoryInterfaceType = 1;
            }

            if (Widget.widgets[menuParam1Entry].parentId == this.backDialogID) {
               this.atInventoryInterfaceType = 3;
            }
         }

         String text;
         int position;
         if ((menuActionId == 337 || menuActionId == 42 || menuActionId == 792 || menuActionId == 322) && (position = (text = this.menuActionNames[menuActionIdIndex]).indexOf("@whi@")) != -1) {
            String name = text.substring(position + 5).trim();

            while (name.startsWith("<img=")) {
               name = name.substring(7);
            }

            long encodedName = NameUtils.encodeBase37(name);
            if (menuActionId == 337) {
               this.addFriend(encodedName);
            }

            if (menuActionId == 42) {
               this.addIgnore(encodedName);
            }

            if (menuActionId == 792) {
               this.removeFriend(encodedName);
            }

            if (menuActionId == 322) {
               this.removeIgnore(encodedName);
            }
         }

         if (menuActionId == 53) {
            this.outgoingBuffer.writeOpcode(135);
            int baseY = atInventoryIndexOrMenuParam0;

            for (int bankTabIndex2 = 0; bankTabIndex2 < bankTabs.length; bankTabIndex2++) {
               BankTab bankTab6 = bankTabs[bankTabIndex2];
               if (menuParam1Entry == bankTab6.getContainerWidgetId() && bankTab6.getSearchResultSlots().size() != 0) {
                  baseY = (Integer)bankTab6.getSearchResultSlots().get(atInventoryIndexOrMenuParam0);
               }
            }

            this.outgoingBuffer.writeShortLittleEndian(baseY);
            this.outgoingBuffer.writeShortAdded(menuParam1Entry);
            this.outgoingBuffer.writeShortLittleEndian(menuParam2Entry);
            this.atInventoryLoopCycle = 0;
            this.atInventoryInterface = menuParam1Entry;
            this.atInventoryIndex = atInventoryIndexOrMenuParam0;
            this.atInventoryInterfaceType = 2;
            if (Widget.widgets[menuParam1Entry].parentId == this.openInterfaceId) {
               this.atInventoryInterfaceType = 1;
            }

            if (Widget.widgets[menuParam1Entry].parentId == this.backDialogID) {
               this.atInventoryInterfaceType = 3;
            }
         }

         int scalar2 = -1;

         for (int bankTabIndex3 = 0; bankTabIndex3 < bankTabs.length; bankTabIndex3++) {
            BankTab bankTab2 = bankTabs[bankTabIndex3];
            if (menuParam1Entry == bankTab2.getActionWidgetId()) {
               if (getBankTabCount() > bankTabIndex3) {
                  scalar2 = bankTabIndex3;
                  break;
               }

               if (getBankTabCount() == bankTabIndex3) {
                  this.pushMessage("To create a new tab, drag items from your bank onto this tab.", 0, "", 0, 0, 0);
                  break;
               }
            }
         }

         if (scalar2 != -1 && !fetchMusic) {
            this.selectBankTab(scalar2, false);
         }

         switch (menuParam1Entry) {
            case 18786:
               server = "";
               this.savedBankScrollPosition = Widget.widgets[5385].scrollPosition;
               this.messagePromptRaised = false;
               this.inputDialogState = 2;
               this.amountOrNameInput = "";
               this.inputTaken = true;
               this.updateBankSearch();
               break;
            case 18934:
            case 18937:
               this.amountOrNameInput = "";
               this.clanChatMode = 0;
               itemSearchSpawnMode = false;
               this.inputDialogState = 3;
         }

         if (menuActionId == 539) {
            this.outgoingBuffer.writeOpcode(16);
            this.outgoingBuffer.writeShortAdded(menuParam2Entry);
            this.outgoingBuffer.writeShortLittleEndianAdded(atInventoryIndexOrMenuParam0);
            this.outgoingBuffer.writeShortLittleEndianAdded(menuParam1Entry);
            this.atInventoryLoopCycle = 0;
            this.atInventoryInterface = menuParam1Entry;
            this.atInventoryIndex = atInventoryIndexOrMenuParam0;
            this.atInventoryInterfaceType = 2;
            if (Widget.widgets[menuParam1Entry].parentId == this.openInterfaceId) {
               this.atInventoryInterfaceType = 1;
            }

            if (Widget.widgets[menuParam1Entry].parentId == this.backDialogID) {
               this.atInventoryInterfaceType = 3;
            }
         }

         String text2;
         int position2;
         if ((menuActionId == 484 || menuActionId == 6) && (position2 = (text2 = this.menuActionNames[menuActionIdIndex]).indexOf("@whi@")) != -1) {
            String text3 = NameUtils.formatDisplayName(NameUtils.decodeBase37(NameUtils.encodeBase37(text2.substring(position2 + 5).trim())));
            boolean localFlag = false;

            for (int playerIndex = 0; playerIndex < this.playerCount; playerIndex++) {
               Player player2;
               if ((player2 = this.players[this.playerIndices[playerIndex]]) != null && player2.name != null && player2.name.equalsIgnoreCase(text3)) {
                  this.doWalkTo(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, player2.pathY[0], localPlayer.pathX[0], false, player2.pathX[0]);
                  if (menuActionId == 484) {
                     this.outgoingBuffer.writeOpcode(139);
                     this.outgoingBuffer.writeShortLittleEndian(this.playerIndices[playerIndex]);
                  }

                  if (menuActionId == 6) {
                     if ((playerInteractionNoiseCounter += menuParam2Entry) >= 90) {
                        this.outgoingBuffer.writeOpcode(136);
                        playerInteractionNoiseCounter = 0;
                     }

                     this.outgoingBuffer.writeOpcode(128);
                     this.outgoingBuffer.writeShort(this.playerIndices[playerIndex]);
                  }

                  localFlag = true;
                  break;
               }
            }

            if (!localFlag) {
               this.pushMessage("Unable to find " + text3, 0, "", 0, 0, 0);
            }
         }

         String text4;
         int position3;
         if (menuActionId == 1999 && (position3 = (text4 = this.menuActionNames[menuActionIdIndex]).indexOf("@whi@")) != -1) {
            String text5 = text4.substring(position3 + 5).trim();

            while (text5.startsWith("<img=")) {
               text5 = text5.substring(7);
            }

            String text6 = NameUtils.formatDisplayName(NameUtils.decodeBase37(NameUtils.encodeBase37(text5)));
            this.outgoingBuffer.writeOpcode(103);
            String text7 = "inv " + text6;
            this.outgoingBuffer.writeByte(text7.length() + 1);
            this.outgoingBuffer.writeString(text7);
         }

         if (menuActionId == 870) {
            this.outgoingBuffer.writeOpcode(53);
            this.outgoingBuffer.writeShort(atInventoryIndexOrMenuParam0);
            this.outgoingBuffer.writeShortAdded(this.selectedItemSlot);
            this.outgoingBuffer.writeShortLittleEndianAdded(menuParam2Entry);
            this.outgoingBuffer.writeShort(this.selectedItemWidgetId);
            this.outgoingBuffer.writeShortLittleEndian(this.selectedItemId);
            this.outgoingBuffer.writeShort(menuParam1Entry);
            this.atInventoryLoopCycle = 0;
            this.atInventoryInterface = menuParam1Entry;
            this.atInventoryIndex = atInventoryIndexOrMenuParam0;
            this.atInventoryInterfaceType = 2;
            if (Widget.widgets[menuParam1Entry].parentId == this.openInterfaceId) {
               this.atInventoryInterfaceType = 1;
            }

            if (Widget.widgets[menuParam1Entry].parentId == this.backDialogID) {
               this.atInventoryInterfaceType = 3;
            }
         }

         if (menuActionId == 847) {
            this.outgoingBuffer.writeOpcode(87);
            this.outgoingBuffer.writeShortAdded(menuParam2Entry);
            this.outgoingBuffer.writeShort(menuParam1Entry);
            this.outgoingBuffer.writeShortAdded(atInventoryIndexOrMenuParam0);
            this.atInventoryLoopCycle = 0;
            this.atInventoryInterface = menuParam1Entry;
            this.atInventoryIndex = atInventoryIndexOrMenuParam0;
            this.atInventoryInterfaceType = 2;
            if (Widget.widgets[menuParam1Entry].parentId == this.openInterfaceId) {
               this.atInventoryInterfaceType = 1;
            }

            if (Widget.widgets[menuParam1Entry].parentId == this.backDialogID) {
               this.atInventoryInterfaceType = 3;
            }
         }

         if (menuActionId == 626) {
            Widget widget6 = Widget.widgets[menuParam1Entry];
            this.spellSelected = 1;
            selectedSpellHighlightWidgetId = widget6.id;
            this.selectedSpellWidgetId = menuParam1Entry;
            this.spellUsableOn = widget6.spellUsableOn;
            this.itemSelected = 0;
            this.needDrawTabArea = true;
            String text8 = widget6.selectedActionName;
            if (widget6.selectedActionName.indexOf(" ") != -1) {
               text8 = text8.substring(0, text8.indexOf(" "));
            }

            String text9 = widget6.selectedActionName;
            if (widget6.selectedActionName.indexOf(" ") != -1) {
               text9 = text9.substring(text9.indexOf(" ") + 1);
            }

            this.spellTooltip = text8 + " " + widget6.spellName + " " + text9;
            if (this.spellUsableOn == 16) {
               this.needDrawTabArea = true;
               this.currentTab = 3;
               this.tabAreaAltered = true;
               return;
            }
         } else {
            if (menuActionId == 78) {
               this.outgoingBuffer.writeOpcode(117);
               int baseY2 = atInventoryIndexOrMenuParam0;

               for (int bankTabIndex4 = 0; bankTabIndex4 < bankTabs.length; bankTabIndex4++) {
                  BankTab bankTab3 = bankTabs[bankTabIndex4];
                  if (menuParam1Entry == bankTab3.getContainerWidgetId() && bankTab3.getSearchResultSlots().size() != 0) {
                     baseY2 = (Integer)bankTab3.getSearchResultSlots().get(atInventoryIndexOrMenuParam0);
                  }
               }

               this.outgoingBuffer.writeShortLittleEndianAdded(menuParam1Entry);
               this.outgoingBuffer.writeShortLittleEndianAdded(menuParam2Entry);
               this.outgoingBuffer.writeShortLittleEndian(baseY2);
               this.atInventoryLoopCycle = 0;
               this.atInventoryInterface = menuParam1Entry;
               this.atInventoryIndex = atInventoryIndexOrMenuParam0;
               this.atInventoryInterfaceType = 2;
               if (Widget.widgets[menuParam1Entry].parentId == this.openInterfaceId) {
                  this.atInventoryInterfaceType = 1;
               }

               if (Widget.widgets[menuParam1Entry].parentId == this.backDialogID) {
                  this.atInventoryInterfaceType = 3;
               }
            }

            if (menuActionId == 27 && (player = this.players[menuParam2Entry]) != null) {
               this.doWalkTo(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, player.pathY[0], localPlayer.pathX[0], false, player.pathX[0]);
               this.crossX = super.clickX;
               this.crossY = super.clickY;
               this.crossType = 2;
               this.crossIndex = 0;
               if ((playerActionNoiseCounter += menuParam2Entry) >= 54) {
                  this.outgoingBuffer.writeOpcode(189);
                  this.outgoingBuffer.writeByte(234);
                  playerActionNoiseCounter = 0;
               }

               this.outgoingBuffer.writeOpcode(73);
               this.outgoingBuffer.writeShortLittleEndian(menuParam2Entry);
            }

            if (menuActionId == 213) {
               if (!this.doWalkTo(2, 0, 0, 0, localPlayer.pathY[0], 0, 0, menuParam1Entry, localPlayer.pathX[0], false, atInventoryIndexOrMenuParam0)) {
                  this.doWalkTo(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, menuParam1Entry, localPlayer.pathX[0], false, atInventoryIndexOrMenuParam0);
               }

               this.crossX = super.clickX;
               this.crossY = super.clickY;
               this.crossType = 2;
               this.crossIndex = 0;
               this.outgoingBuffer.writeOpcode(79);
               this.outgoingBuffer.writeShortLittleEndian(menuParam1Entry + this.baseY);
               this.outgoingBuffer.writeShort(menuParam2Entry);
               this.outgoingBuffer.writeShortAdded(atInventoryIndexOrMenuParam0 + this.baseX);
            }

            if (menuActionId == 999) {
               if (screenMode != 0 && chatViewMode == 0) {
                  this.chatMessagesVisible = !this.chatMessagesVisible;
               }

               tiara = 0;
               chatViewMode = 0;
               this.inputTaken = true;
            }

            if (menuActionId == 998) {
               if (screenMode != 0 && chatViewMode == 5) {
                  this.chatMessagesVisible = !this.chatMessagesVisible;
               }

               tiara = 1;
               chatViewMode = 5;
               this.inputTaken = true;
            }

            if (menuActionId == 997) {
               this.publicChatMode = 3;
               this.inputTaken = true;
               this.outgoingBuffer.writeOpcode(95);
               this.outgoingBuffer.writeByte(this.publicChatMode);
               this.outgoingBuffer.writeByte(this.privateChatMode);
               this.outgoingBuffer.writeByte(this.tradeMode);
            }

            if (menuActionId == 996) {
               this.publicChatMode = 2;
               this.inputTaken = true;
               this.outgoingBuffer.writeOpcode(95);
               this.outgoingBuffer.writeByte(this.publicChatMode);
               this.outgoingBuffer.writeByte(this.privateChatMode);
               this.outgoingBuffer.writeByte(this.tradeMode);
            }

            if (menuActionId == 995) {
               this.publicChatMode = 1;
               this.inputTaken = true;
               this.outgoingBuffer.writeOpcode(95);
               this.outgoingBuffer.writeByte(this.publicChatMode);
               this.outgoingBuffer.writeByte(this.privateChatMode);
               this.outgoingBuffer.writeByte(this.tradeMode);
            }

            if (menuActionId == 994) {
               this.publicChatMode = 0;
               this.inputTaken = true;
               this.outgoingBuffer.writeOpcode(95);
               this.outgoingBuffer.writeByte(this.publicChatMode);
               this.outgoingBuffer.writeByte(this.privateChatMode);
               this.outgoingBuffer.writeByte(this.tradeMode);
            }

            if (menuActionId == 993) {
               if (screenMode != 0 && chatViewMode == 1) {
                  this.chatMessagesVisible = !this.chatMessagesVisible;
               }

               tiara = 2;
               chatViewMode = 1;
               this.inputTaken = true;
            }

            if (menuActionId == 992) {
               this.privateChatMode = 2;
               this.inputTaken = true;
               this.outgoingBuffer.writeOpcode(95);
               this.outgoingBuffer.writeByte(this.publicChatMode);
               this.outgoingBuffer.writeByte(this.privateChatMode);
               this.outgoingBuffer.writeByte(this.tradeMode);
            }

            if (menuActionId == 991) {
               this.privateChatMode = 1;
               this.inputTaken = true;
               this.outgoingBuffer.writeOpcode(95);
               this.outgoingBuffer.writeByte(this.publicChatMode);
               this.outgoingBuffer.writeByte(this.privateChatMode);
               this.outgoingBuffer.writeByte(this.tradeMode);
            }

            if (menuActionId == 990) {
               this.privateChatMode = 0;
               this.inputTaken = true;
               this.outgoingBuffer.writeOpcode(95);
               this.outgoingBuffer.writeByte(this.publicChatMode);
               this.outgoingBuffer.writeByte(this.privateChatMode);
               this.outgoingBuffer.writeByte(this.tradeMode);
            }

            if (menuActionId == 989) {
               if (screenMode != 0 && chatViewMode == 2) {
                  this.chatMessagesVisible = !this.chatMessagesVisible;
               }

               tiara = 3;
               chatViewMode = 2;
               this.inputTaken = true;
            }

            if (menuActionId == 987) {
               this.tradeMode = 2;
               this.inputTaken = true;
               this.outgoingBuffer.writeOpcode(95);
               this.outgoingBuffer.writeByte(this.publicChatMode);
               this.outgoingBuffer.writeByte(this.privateChatMode);
               this.outgoingBuffer.writeByte(this.tradeMode);
            }

            if (menuActionId == 986) {
               this.tradeMode = 1;
               this.inputTaken = true;
               this.outgoingBuffer.writeOpcode(95);
               this.outgoingBuffer.writeByte(this.publicChatMode);
               this.outgoingBuffer.writeByte(this.privateChatMode);
               this.outgoingBuffer.writeByte(this.tradeMode);
            }

            if (menuActionId == 985) {
               this.tradeMode = 0;
               this.inputTaken = true;
               this.outgoingBuffer.writeOpcode(95);
               this.outgoingBuffer.writeByte(this.publicChatMode);
               this.outgoingBuffer.writeByte(this.privateChatMode);
               this.outgoingBuffer.writeByte(this.tradeMode);
            }

            if (menuActionId == 984) {
               if (screenMode != 0 && chatViewMode == 3) {
                  this.chatMessagesVisible = !this.chatMessagesVisible;
               }

               tiara = 5;
               chatViewMode = 3;
               this.inputTaken = true;
            }

            if (menuActionId == 632) {
               this.outgoingBuffer.writeOpcode(145);
               int scalar3 = atInventoryIndexOrMenuParam0;

               for (int bankTabIndex5 = 0; bankTabIndex5 < bankTabs.length; bankTabIndex5++) {
                  BankTab bankTab4 = bankTabs[bankTabIndex5];
                  if (menuParam1Entry == bankTab4.getContainerWidgetId() && bankTab4.getSearchResultSlots().size() != 0) {
                     scalar3 = (Integer)bankTab4.getSearchResultSlots().get(atInventoryIndexOrMenuParam0);
                  }
               }

               this.outgoingBuffer.writeShortAdded(menuParam1Entry);
               this.outgoingBuffer.writeShortAdded(scalar3);
               this.outgoingBuffer.writeShortAdded(menuParam2Entry);
               this.atInventoryLoopCycle = 0;
               this.atInventoryInterface = menuParam1Entry;
               this.atInventoryIndex = atInventoryIndexOrMenuParam0;
               this.atInventoryInterfaceType = 2;
               if (Widget.widgets[menuParam1Entry].parentId == this.openInterfaceId) {
                  this.atInventoryInterfaceType = 1;
               }

               if (Widget.widgets[menuParam1Entry].parentId == this.backDialogID) {
                  this.atInventoryInterfaceType = 3;
               }
            }

            if (menuActionId == 493) {
               this.outgoingBuffer.writeOpcode(75);
               this.outgoingBuffer.writeShortLittleEndianAdded(menuParam1Entry);
               this.outgoingBuffer.writeShortLittleEndian(atInventoryIndexOrMenuParam0);
               this.outgoingBuffer.writeShortAdded(menuParam2Entry);
               this.atInventoryLoopCycle = 0;
               this.atInventoryInterface = menuParam1Entry;
               this.atInventoryIndex = atInventoryIndexOrMenuParam0;
               this.atInventoryInterfaceType = 2;
               if (Widget.widgets[menuParam1Entry].parentId == this.openInterfaceId) {
                  this.atInventoryInterfaceType = 1;
               }

               if (Widget.widgets[menuParam1Entry].parentId == this.backDialogID) {
                  this.atInventoryInterfaceType = 3;
               }
            }

            if (menuActionId == 652) {
               if (!this.doWalkTo(2, 0, 0, 0, localPlayer.pathY[0], 0, 0, menuParam1Entry, localPlayer.pathX[0], false, atInventoryIndexOrMenuParam0)) {
                  this.doWalkTo(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, menuParam1Entry, localPlayer.pathX[0], false, atInventoryIndexOrMenuParam0);
               }

               this.crossX = super.clickX;
               this.crossY = super.clickY;
               this.crossType = 2;
               this.crossIndex = 0;
               this.outgoingBuffer.writeOpcode(156);
               this.outgoingBuffer.writeShortAdded(atInventoryIndexOrMenuParam0 + this.baseX);
               this.outgoingBuffer.writeShortLittleEndian(menuParam1Entry + this.baseY);
               this.outgoingBuffer.writeShortLittleEndianAdded(menuParam2Entry);
            }

            if (menuActionId == 94) {
               if (!this.doWalkTo(2, 0, 0, 0, localPlayer.pathY[0], 0, 0, menuParam1Entry, localPlayer.pathX[0], false, atInventoryIndexOrMenuParam0)) {
                  this.doWalkTo(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, menuParam1Entry, localPlayer.pathX[0], false, atInventoryIndexOrMenuParam0);
               }

               this.crossX = super.clickX;
               this.crossY = super.clickY;
               this.crossType = 2;
               this.crossIndex = 0;
               this.outgoingBuffer.writeOpcode(181);
               this.outgoingBuffer.writeShortLittleEndian(menuParam1Entry + this.baseY);
               this.outgoingBuffer.writeShort(menuParam2Entry);
               this.outgoingBuffer.writeShortLittleEndian(atInventoryIndexOrMenuParam0 + this.baseX);
               this.outgoingBuffer.writeShortAdded(this.selectedSpellWidgetId);
            }

            if (menuActionId == 646) {
               this.outgoingBuffer.writeOpcode(185);
               this.outgoingBuffer.writeShort(menuParam1Entry);
               Widget widget2;
               if ((widget2 = Widget.widgets[menuParam1Entry]).valueIndexArray != null && widget2.valueIndexArray[0][0] == 5) {
                  int varpIndex = widget2.valueIndexArray[0][1];
                  if (this.varps[varpIndex] != widget2.scriptCompareValues[0]) {
                     this.varps[varpIndex] = widget2.scriptCompareValues[0];
                      if (varpIndex == 43) {
                         this.pendingCombatStyleValue = widget2.scriptCompareValues[0];
                         this.pendingCombatStyleUntilMillis = System.currentTimeMillis() + 2000L;
                         this.serverVarps[varpIndex] = this.pendingCombatStyleValue;
                      }
                     this.applyVarpSetting(varpIndex);
                     this.needDrawTabArea = true;
                  }
               }
            }

            if (menuActionId == 225 && (npc = this.npcs[menuParam2Entry]) != null) {
               this.doWalkTo(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, npc.pathY[0], localPlayer.pathX[0], false, npc.pathX[0]);
               this.crossX = super.clickX;
               this.crossY = super.clickY;
               this.crossType = 2;
               this.crossIndex = 0;
               if ((npcInteractionNoiseCounter += menuParam2Entry) >= 85) {
                  this.outgoingBuffer.writeOpcode(230);
                  this.outgoingBuffer.writeByte(239);
                  npcInteractionNoiseCounter = 0;
               }

               this.outgoingBuffer.writeOpcode(17);
               this.outgoingBuffer.writeShortLittleEndianAdded(menuParam2Entry);
            }

            if (menuActionId == 965 && (npc = this.npcs[menuParam2Entry]) != null) {
               this.doWalkTo(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, npc.pathY[0], localPlayer.pathX[0], false, npc.pathX[0]);
               this.crossX = super.clickX;
               this.crossY = super.clickY;
               this.crossType = 2;
               this.crossIndex = 0;
               if (++keepaliveCounter >= 96) {
                  this.outgoingBuffer.writeOpcode(152);
                  this.outgoingBuffer.writeByte(88);
                  keepaliveCounter = 0;
               }

               this.outgoingBuffer.writeOpcode(21);
               this.outgoingBuffer.writeShort(menuParam2Entry);
            }

            if (menuActionId == 413 && (npc = this.npcs[menuParam2Entry]) != null) {
               this.doWalkTo(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, npc.pathY[0], localPlayer.pathX[0], false, npc.pathX[0]);
               this.crossX = super.clickX;
               this.crossY = super.clickY;
               this.crossType = 2;
               this.crossIndex = 0;
               this.outgoingBuffer.writeOpcode(131);
               this.outgoingBuffer.writeShortLittleEndianAdded(menuParam2Entry);
               this.outgoingBuffer.writeShortAdded(this.selectedSpellWidgetId);
            }

            if (menuActionId == 200) {
               this.closeTopInterfaces();
            }

            if (menuActionId == 1025 && (npc = this.npcs[menuParam2Entry]) != null) {
               NpcDefinition npcDefinition;
               if ((npcDefinition = npc.definition).childIds != null) {
                  npcDefinition = npcDefinition.morph();
               }

               if (npcDefinition != null) {
                  String text10;
                  if (npcDefinition.description != null) {
                     text10 = new String(npcDefinition.description);
                  } else {
                     text10 = "It's a " + npcDefinition.name + ".";
                  }

                  this.pushMessage(text10, 0, "", 0, 0, 0);
               }
            }

            if (menuActionId == 900) {
               this.clickObject(menuParam2Entry, menuParam1Entry, atInventoryIndexOrMenuParam0);
               this.outgoingBuffer.writeOpcode(252);
               this.outgoingBuffer.writeShortLittleEndianAdded(menuParam2Entry >> 14 & 32767);
               this.outgoingBuffer.writeShortLittleEndian(menuParam1Entry + this.baseY);
               this.outgoingBuffer.writeShortAdded(atInventoryIndexOrMenuParam0 + this.baseX);
            }

            if (menuActionId == 412 && (npc = this.npcs[menuParam2Entry]) != null) {
               this.doWalkTo(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, npc.pathY[0], localPlayer.pathX[0], false, npc.pathX[0]);
               this.crossX = super.clickX;
               this.crossY = super.clickY;
               this.crossType = 2;
               this.crossIndex = 0;
               this.outgoingBuffer.writeOpcode(72);
               this.outgoingBuffer.writeShortAdded(menuParam2Entry);
            }

            if (menuActionId == 365 && (player = this.players[menuParam2Entry]) != null) {
               this.doWalkTo(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, player.pathY[0], localPlayer.pathX[0], false, player.pathX[0]);
               this.crossX = super.clickX;
               this.crossY = super.clickY;
               this.crossType = 2;
               this.crossIndex = 0;
               this.outgoingBuffer.writeOpcode(249);
               this.outgoingBuffer.writeShortAdded(menuParam2Entry);
               this.outgoingBuffer.writeShortLittleEndian(this.selectedSpellWidgetId);
            }

            if (menuActionId == 729 && (player = this.players[menuParam2Entry]) != null) {
               this.doWalkTo(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, player.pathY[0], localPlayer.pathX[0], false, player.pathX[0]);
               this.crossX = super.clickX;
               this.crossY = super.clickY;
               this.crossType = 2;
               this.crossIndex = 0;
               this.outgoingBuffer.writeOpcode(39);
               this.outgoingBuffer.writeShortLittleEndian(menuParam2Entry);
            }

            if (menuActionId == 577 && (player = this.players[menuParam2Entry]) != null) {
               this.doWalkTo(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, player.pathY[0], localPlayer.pathX[0], false, player.pathX[0]);
               this.crossX = super.clickX;
               this.crossY = super.clickY;
               this.crossType = 2;
               this.crossIndex = 0;
               this.outgoingBuffer.writeOpcode(139);
               this.outgoingBuffer.writeShortLittleEndian(menuParam2Entry);
            }

            if (menuActionId == 956 && this.clickObject(menuParam2Entry, menuParam1Entry, atInventoryIndexOrMenuParam0)) {
               this.outgoingBuffer.writeOpcode(35);
               this.outgoingBuffer.writeShortLittleEndian(atInventoryIndexOrMenuParam0 + this.baseX);
               this.outgoingBuffer.writeShortAdded(this.selectedSpellWidgetId);
               this.outgoingBuffer.writeShortAdded(menuParam1Entry + this.baseY);
               this.outgoingBuffer.writeShortLittleEndian(menuParam2Entry >> 14 & 32767);
            }

            if (menuActionId == 567) {
               if (!this.doWalkTo(2, 0, 0, 0, localPlayer.pathY[0], 0, 0, menuParam1Entry, localPlayer.pathX[0], false, atInventoryIndexOrMenuParam0)) {
                  this.doWalkTo(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, menuParam1Entry, localPlayer.pathX[0], false, atInventoryIndexOrMenuParam0);
               }

               this.crossX = super.clickX;
               this.crossY = super.clickY;
               this.crossType = 2;
               this.crossIndex = 0;
               this.outgoingBuffer.writeOpcode(23);
               this.outgoingBuffer.writeShortLittleEndian(menuParam1Entry + this.baseY);
               this.outgoingBuffer.writeShortLittleEndian(menuParam2Entry);
               this.outgoingBuffer.writeShortLittleEndian(atInventoryIndexOrMenuParam0 + this.baseX);
            }

            if (menuActionId == 867) {
               if ((menuParam2Entry & 3) == 0) {
                  inventoryActionNoiseCounter++;
               }

               if (inventoryActionNoiseCounter >= 59) {
                  this.outgoingBuffer.writeOpcode(200);
                  this.outgoingBuffer.writeShort(25501);
                  inventoryActionNoiseCounter = 0;
               }

               this.outgoingBuffer.writeOpcode(43);
               int scalar4 = atInventoryIndexOrMenuParam0;

               for (int bankTabIndex6 = 0; bankTabIndex6 < bankTabs.length; bankTabIndex6++) {
                  BankTab bankTab5 = bankTabs[bankTabIndex6];
                  if (menuParam1Entry == bankTab5.getContainerWidgetId() && bankTab5.getSearchResultSlots().size() != 0) {
                     scalar4 = (Integer)bankTab5.getSearchResultSlots().get(atInventoryIndexOrMenuParam0);
                  }
               }

               this.outgoingBuffer.writeShortLittleEndian(menuParam1Entry);
               this.outgoingBuffer.writeShortAdded(menuParam2Entry);
               this.outgoingBuffer.writeShortAdded(scalar4);
               this.atInventoryLoopCycle = 0;
               this.atInventoryInterface = menuParam1Entry;
               this.atInventoryIndex = atInventoryIndexOrMenuParam0;
               this.atInventoryInterfaceType = 2;
               if (Widget.widgets[menuParam1Entry].parentId == this.openInterfaceId) {
                  this.atInventoryInterfaceType = 1;
               }

               if (Widget.widgets[menuParam1Entry].parentId == this.backDialogID) {
                  this.atInventoryInterfaceType = 3;
               }
            }

            if (menuActionId == 543) {
               this.outgoingBuffer.writeOpcode(237);
               this.outgoingBuffer.writeShort(atInventoryIndexOrMenuParam0);
               this.outgoingBuffer.writeShortAdded(menuParam2Entry);
               this.outgoingBuffer.writeShort(menuParam1Entry);
               this.outgoingBuffer.writeShortAdded(this.selectedSpellWidgetId);
               this.atInventoryLoopCycle = 0;
               this.atInventoryInterface = menuParam1Entry;
               this.atInventoryIndex = atInventoryIndexOrMenuParam0;
               this.atInventoryInterfaceType = 2;
               if (Widget.widgets[menuParam1Entry].parentId == this.openInterfaceId) {
                  this.atInventoryInterfaceType = 1;
               }

               if (Widget.widgets[menuParam1Entry].parentId == this.backDialogID) {
                  this.atInventoryInterfaceType = 3;
               }
            }

            if (menuActionId == 1250) {
               this.inputDialogState = 0;
               this.inputTaken = true;
               this.outgoingBuffer.writeOpcode(19);
               this.outgoingBuffer.writeShort(this.chatTypeView);
            }

            if (menuActionId == 1251) {
               this.outgoingBuffer.writeOpcode(103);
               String text11 = "sitem " + this.chatTypeView + " " + this.itemSpawnAmount;
               this.outgoingBuffer.writeByte(text11.length() + 1);
               this.outgoingBuffer.writeString(text11);
               this.inputDialogState = 0;
               this.inputTaken = true;
            }

            String text12;
            int position4;
            if (menuActionId == 606 && (position4 = (text12 = this.menuActionNames[menuActionIdIndex]).indexOf("@whi@")) != -1) {
               if (this.openInterfaceId == -1 && this.fullscreenInterfaceId == -1) {
                  this.closeTopInterfaces();
                  this.reportAbuseInput = text12.substring(position4 + 5).trim();
                  this.canMute = false;
                  Widget[] widgets = Widget.widgets;
                  int widgetsLengthOrWidgets = Widget.widgets.length;

                  for (int widgetIndex = 0; widgetIndex < widgetsLengthOrWidgets; widgetIndex++) {
                     Widget widget3;
                     if ((widget3 = widgets[widgetIndex]) != null && widget3.contentType == 600) {
                        this.reportAbuseInterfaceID = this.openInterfaceId = widget3.parentId;
                        break;
                     }
                  }
               } else {
                  this.pushMessage("Please close the interface you have open before using 'report abuse'", 0, "", 0, 0, 0);
               }
            }

            if (menuActionId == 491 && (player = this.players[menuParam2Entry]) != null) {
               this.doWalkTo(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, player.pathY[0], localPlayer.pathX[0], false, player.pathX[0]);
               this.crossX = super.clickX;
               this.crossY = super.clickY;
               this.crossType = 2;
               this.crossIndex = 0;
               this.outgoingBuffer.writeOpcode(14);
               this.outgoingBuffer.writeShort(menuParam2Entry);
               this.outgoingBuffer.writeShortLittleEndian(this.selectedItemSlot);
            }

            String text13;
            int position5;
            if (menuActionId == 639 && (position5 = (text13 = this.menuActionNames[menuActionIdIndex]).indexOf("@whi@")) != -1) {
               long encodeBase372 = NameUtils.encodeBase37(text13.substring(position5 + 5).trim());
               int friendWorldIndex = -1;

               for (int friendEncodedNameIndex = 0; friendEncodedNameIndex < this.friendCount; friendEncodedNameIndex++) {
                  if (this.friendEncodedNames[friendEncodedNameIndex] == encodeBase372) {
                     friendWorldIndex = friendEncodedNameIndex;
                     break;
                  }
               }

               if (friendWorldIndex != -1 && this.friendWorlds[friendWorldIndex] > 9) {
                  this.inputTaken = true;
                  this.inputDialogState = 0;
                  this.messagePromptRaised = true;
                  this.promptInput = "";
                  this.friendsListAction = 3;
                  this.privateMessageTarget = this.friendEncodedNames[friendWorldIndex];
                  this.promptMessage = "Enter message to send to " + this.friendNames[friendWorldIndex];
               } else {
                  this.pushMessage("That player is currently offline.", 0, "", 0, 0, 0);
               }
            }

            if (menuActionId == 454) {
               this.outgoingBuffer.writeOpcode(41);
               this.outgoingBuffer.writeShort(menuParam2Entry);
               this.outgoingBuffer.writeShortAdded(atInventoryIndexOrMenuParam0);
               this.outgoingBuffer.writeShortAdded(menuParam1Entry);
               this.atInventoryLoopCycle = 0;
               this.atInventoryInterface = menuParam1Entry;
               this.atInventoryIndex = atInventoryIndexOrMenuParam0;
               this.atInventoryInterfaceType = 2;
               if (Widget.widgets[menuParam1Entry].parentId == this.openInterfaceId) {
                  this.atInventoryInterfaceType = 1;
               }

               if (Widget.widgets[menuParam1Entry].parentId == this.backDialogID) {
                  this.atInventoryInterfaceType = 3;
               }
            }

            if (menuActionId == 478 && (npc = this.npcs[menuParam2Entry]) != null) {
               this.doWalkTo(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, npc.pathY[0], localPlayer.pathX[0], false, npc.pathX[0]);
               this.crossX = super.clickX;
               this.crossY = super.clickY;
               this.crossType = 2;
               this.crossIndex = 0;
               if ((menuParam2Entry & 3) == 0) {
                  audioNoiseCounter++;
               }

               if (audioNoiseCounter >= 53) {
                  this.outgoingBuffer.writeOpcode(85);
                  this.outgoingBuffer.writeByte(66);
                  audioNoiseCounter = 0;
               }

               this.outgoingBuffer.writeOpcode(18);
               this.outgoingBuffer.writeShortLittleEndian(menuParam2Entry);
            }

            if (menuActionId == 113) {
               this.clickObject(menuParam2Entry, menuParam1Entry, atInventoryIndexOrMenuParam0);
               this.outgoingBuffer.writeOpcode(70);
               this.outgoingBuffer.writeShortLittleEndian(atInventoryIndexOrMenuParam0 + this.baseX);
               this.outgoingBuffer.writeShort(menuParam1Entry + this.baseY);
               this.outgoingBuffer.writeShortLittleEndianAdded(menuParam2Entry >> 14 & 32767);
            }

            if (menuActionId == 872) {
               this.clickObject(menuParam2Entry, menuParam1Entry, atInventoryIndexOrMenuParam0);
               this.outgoingBuffer.writeOpcode(234);
               this.outgoingBuffer.writeShortLittleEndianAdded(atInventoryIndexOrMenuParam0 + this.baseX);
               this.outgoingBuffer.writeShortAdded(menuParam2Entry >> 14 & 32767);
               this.outgoingBuffer.writeShortLittleEndianAdded(menuParam1Entry + this.baseY);
            }

            if (menuActionId == 502) {
               this.clickObject(menuParam2Entry, menuParam1Entry, atInventoryIndexOrMenuParam0);
               this.outgoingBuffer.writeOpcode(132);
               this.outgoingBuffer.writeShortLittleEndianAdded(atInventoryIndexOrMenuParam0 + this.baseX);
               this.outgoingBuffer.writeShort(menuParam2Entry >> 14 & 32767);
               this.outgoingBuffer.writeShortAdded(menuParam1Entry + this.baseY);
            }

            if (menuActionId == 1125) {
               this.atInventoryLoopCycle = 0;
               this.atInventoryInterface = menuParam1Entry;
               this.atInventoryIndex = atInventoryIndexOrMenuParam0;
               this.atInventoryInterfaceType = 2;
               ItemDefinition itemDefinition = ItemDefinition.lookup(menuParam2Entry);
               Widget widget4;
               String text14;
               if ((widget4 = Widget.widgets[menuParam1Entry]) != null && widget4.inventoryAmounts[atInventoryIndexOrMenuParam0] >= 100000) {
                  text14 = this.numberFormat.format(widget4.inventoryAmounts[atInventoryIndexOrMenuParam0]) + " x " + itemDefinition.name;
               } else if (itemDefinition.description != null) {
                  text14 = new String(itemDefinition.description);
               } else {
                  text14 = "It's a " + itemDefinition.name + ".";
               }

               this.pushMessage(text14, 0, "", 0, 0, 0);
               if (showAlchValueOnExamine && itemDefinition.getLowAlchValue() > 0) {
                  String text15 = itemDefinition.name
                     + " (Low Alch: "
                     + this.numberFormat.format(itemDefinition.getLowAlchValue())
                     + ", High Alch: "
                     + this.numberFormat.format(itemDefinition.getHighAlchValue())
                     + ")";
                  this.pushMessage(text15, 0, "", 0, 0, 0);
               }
            }

            if (menuActionId == 169) {
               this.outgoingBuffer.writeOpcode(185);
               this.outgoingBuffer.writeShort(menuParam1Entry);
               Widget widget5;
               if ((widget5 = Widget.widgets[menuParam1Entry]).valueIndexArray != null && widget5.valueIndexArray[0][0] == 5) {
                  int valueIndexArray2 = widget5.valueIndexArray[0][1];
                  this.varps[valueIndexArray2] = 1 - this.varps[valueIndexArray2];
                  this.applyVarpSetting(valueIndexArray2);
                  this.needDrawTabArea = true;
               }
            }

            if (menuActionId == 447) {
               this.itemSelected = 1;
               this.selectedItemSlot = atInventoryIndexOrMenuParam0;
               this.selectedItemWidgetId = menuParam1Entry;
               this.selectedItemId = menuParam2Entry;
               this.selectedItemName = ItemDefinition.lookup(menuParam2Entry).name;
               this.spellSelected = 0;
               this.needDrawTabArea = true;
               return;
            }

            if (menuActionId == 1226) {
               String text16;
               ObjectDefinition objectDefinition;
               if ((objectDefinition = ObjectDefinition.lookup(menuParam2Entry >> 14 & 32767)).description != null) {
                  text16 = new String(objectDefinition.description);
               } else {
                  text16 = "It's a " + objectDefinition.name + ".";
               }

               this.pushMessage(text16, 0, "", 0, 0, 0);
            }

            if (menuActionId == 244) {
               if (!this.doWalkTo(2, 0, 0, 0, localPlayer.pathY[0], 0, 0, menuParam1Entry, localPlayer.pathX[0], false, atInventoryIndexOrMenuParam0)) {
                  this.doWalkTo(2, 0, 1, 0, localPlayer.pathY[0], 1, 0, menuParam1Entry, localPlayer.pathX[0], false, atInventoryIndexOrMenuParam0);
               }

               this.crossX = super.clickX;
               this.crossY = super.clickY;
               this.crossType = 2;
               this.crossIndex = 0;
               this.outgoingBuffer.writeOpcode(253);
               this.outgoingBuffer.writeShortLittleEndian(atInventoryIndexOrMenuParam0 + this.baseX);
               this.outgoingBuffer.writeShortLittleEndianAdded(menuParam1Entry + this.baseY);
               this.outgoingBuffer.writeShortAdded(menuParam2Entry);
            }

            if (menuActionId == 1448) {
               ItemDefinition itemDefinition2;
               String text17;
               if ((itemDefinition2 = ItemDefinition.lookup(menuParam2Entry)).description != null) {
                  text17 = new String(itemDefinition2.description);
               } else {
                  text17 = "It's a " + itemDefinition2.name + ".";
               }

               this.pushMessage(text17, 0, "", 0, 0, 0);
               if (showAlchValueOnExamine && itemDefinition2.getLowAlchValue() > 0) {
                  String text18 = itemDefinition2.name
                     + " (Low Alch: "
                     + this.numberFormat.format(itemDefinition2.getLowAlchValue())
                     + ", High Alch: "
                     + this.numberFormat.format(itemDefinition2.getHighAlchValue())
                     + ")";
                  this.pushMessage(text18, 0, "", 0, 0, 0);
               }
            }

            this.itemSelected = 0;
            this.spellSelected = 0;
            this.needDrawTabArea = true;
         }
      }
   }
   @Override
   public void run() {
      if (this.drawFlames) {
         Client client = this;
         this.drawingFlames = true;

         try {
            long localCurrentTimeMillis = System.currentTimeMillis();
            int scalar = 0;
            int scalar2 = 20;

            while (client.flameThreadRunning) {
               client.flameCycle++;
               client.calcFlamesPosition();
               client.calcFlamesPosition();
               Client sourceClient = client;
               if (client.greenFlameTransition > 0) {
                  for (int activeFlamePaletteIndex2 = 0; activeFlamePaletteIndex2 < 256; activeFlamePaletteIndex2++) {
                     if (sourceClient.greenFlameTransition > 768) {
                        sourceClient.activeFlamePalette[activeFlamePaletteIndex2] = blendColors(sourceClient.warmFlamePalette[activeFlamePaletteIndex2], sourceClient.greenFlamePalette[activeFlamePaletteIndex2], 1024 - sourceClient.greenFlameTransition);
                     } else if (sourceClient.greenFlameTransition > 256) {
                        sourceClient.activeFlamePalette[activeFlamePaletteIndex2] = sourceClient.greenFlamePalette[activeFlamePaletteIndex2];
                     } else {
                        sourceClient.activeFlamePalette[activeFlamePaletteIndex2] = blendColors(sourceClient.greenFlamePalette[activeFlamePaletteIndex2], sourceClient.warmFlamePalette[activeFlamePaletteIndex2], 256 - sourceClient.greenFlameTransition);
                     }
                  }
               } else if (sourceClient.blueFlameTransition > 0) {
                  for (int activeFlamePaletteIndex = 0; activeFlamePaletteIndex < 256; activeFlamePaletteIndex++) {
                     if (sourceClient.blueFlameTransition > 768) {
                        sourceClient.activeFlamePalette[activeFlamePaletteIndex] = blendColors(sourceClient.warmFlamePalette[activeFlamePaletteIndex], sourceClient.blueFlamePalette[activeFlamePaletteIndex], 1024 - sourceClient.blueFlameTransition);
                     } else if (sourceClient.blueFlameTransition > 256) {
                        sourceClient.activeFlamePalette[activeFlamePaletteIndex] = sourceClient.blueFlamePalette[activeFlamePaletteIndex];
                     } else {
                        sourceClient.activeFlamePalette[activeFlamePaletteIndex] = blendColors(sourceClient.blueFlamePalette[activeFlamePaletteIndex], sourceClient.warmFlamePalette[activeFlamePaletteIndex], 256 - sourceClient.blueFlameTransition);
                     }
                  }
               } else {
                  System.arraycopy(sourceClient.warmFlamePalette, 0, sourceClient.activeFlamePalette, 0, 256);
               }

               System.arraycopy(sourceClient.originalFlameRightBackground.pixels, 0, sourceClient.flameRightBackground.pixels, 0, 33920);
               int scalar3 = 0;
               int pixelIndex = 1152;

               for (int flameLineOffsetIndex = 1; flameLineOffsetIndex < 255; flameLineOffsetIndex++) {
                  int localFlameLineOffsets = sourceClient.flameLineOffsets[flameLineOffsetIndex] * (256 - flameLineOffsetIndex) / 256;
                  int position;
                  if ((position = localFlameLineOffsets + 22) < 0) {
                     position = 0;
                  }

                  scalar3 += position;

                  for (int loopIndex = position; loopIndex < 128; loopIndex++) {
                     int activeFlamePaletteIndex3;
                     if ((activeFlamePaletteIndex3 = sourceClient.flameIntensity[scalar3++]) != 0) {
                        int scalar4 = activeFlamePaletteIndex3;
                        int scalar5 = 256 - activeFlamePaletteIndex3;
                        activeFlamePaletteIndex3 = sourceClient.activeFlamePalette[activeFlamePaletteIndex3];
                        int pixel = sourceClient.flameRightBackground.pixels[pixelIndex];
                        sourceClient.flameRightBackground.pixels[pixelIndex++] = ((activeFlamePaletteIndex3 & 16711935) * scalar4 + (pixel & 16711935) * scalar5 & -16711936)
                              + ((activeFlamePaletteIndex3 & 0xFF00) * scalar4 + (pixel & 0xFF00) * scalar5 & 0xFF0000)
                           >> 8;
                     } else {
                        pixelIndex++;
                     }
                  }

                  pixelIndex += position;
               }

               sourceClient.flameRightBackground.drawGraphics(0, sourceClient.graphics, 0);
               System.arraycopy(sourceClient.originalBottomLeftBackground.pixels, 0, sourceClient.bottomLeft0BackgroundTile.pixels, 0, 33920);
               scalar3 = 0;
               pixelIndex = 1176;

               for (int flameLineOffsetIndex2 = 1; flameLineOffsetIndex2 < 255; flameLineOffsetIndex2++) {
                  int flameLineOffsets2 = sourceClient.flameLineOffsets[flameLineOffsetIndex2] * (256 - flameLineOffsetIndex2) / 256;
                  int loopIndex2 = 103 - flameLineOffsets2;
                  pixelIndex += flameLineOffsets2;

                  for (int loopIndex3 = 0; loopIndex3 < loopIndex2; loopIndex3++) {
                     int flameIntensity2;
                     if ((flameIntensity2 = sourceClient.flameIntensity[scalar3++]) != 0) {
                        int scalar6 = flameIntensity2;
                        int scalar7 = 256 - flameIntensity2;
                        flameIntensity2 = sourceClient.activeFlamePalette[flameIntensity2];
                        int pixel2 = sourceClient.bottomLeft0BackgroundTile.pixels[pixelIndex];
                        sourceClient.bottomLeft0BackgroundTile.pixels[pixelIndex++] = ((flameIntensity2 & 16711935) * scalar6 + (pixel2 & 16711935) * scalar7 & -16711936)
                              + ((flameIntensity2 & 0xFF00) * scalar6 + (pixel2 & 0xFF00) * scalar7 & 0xFF0000)
                           >> 8;
                     } else {
                        pixelIndex++;
                     }
                  }

                  scalar3 += 128 - loopIndex2;
                  pixelIndex += 128 - loopIndex2 - flameLineOffsets2;
               }

               sourceClient.bottomLeft0BackgroundTile.drawGraphics(0, sourceClient.graphics, 637);
               if (++scalar > 10) {
                  long currentTimeMillis2;
                  int scalar8 = (int)((currentTimeMillis2 = System.currentTimeMillis()) - localCurrentTimeMillis) / 10 - scalar2;
                  if ((scalar2 = 40 - scalar8) < 5) {
                     scalar2 = 5;
                  }

                  scalar = 0;
                  localCurrentTimeMillis = currentTimeMillis2;
               }

               try {
                  Thread.sleep(scalar2);
               } catch (Exception exception) {
               }
            }
         } catch (Exception exception2) {
         }

         client.drawingFlames = false;
      } else {
         super.run();
      }
   }
   private static PcmStreamMixer createPcmStreamMixer(Component component) {
      Component sourceComponent = component;

      try {
         PcmPlayer pcmPlayer;
         (pcmPlayer = (PcmPlayer)Class.forName("client.JavaSoundPcmPlayer").newInstance()).start(2048);
         audioPlayer = pcmPlayer;
      } catch (Throwable throwable) {
         try {
            audioPlayer = new ComponentPcmPlayer(sourceComponent);
         } catch (Throwable exception) {
            selectAudioFallback: {
               if (System.getProperty("java.vendor").toLowerCase().indexOf("microsoft") >= 0) {
                  try {
                     audioPlayer = new SunAudioPlayer();
                     break selectAudioFallback;
                  } catch (Throwable throwable2) {
                  }
               }

               audioPlayer = new AudioPlayerBase(8000);
            }
         }
      }

      PcmStreamMixer pcmStreamMixer;
      pcmStream = pcmStreamMixer = new PcmStreamMixer();
      return pcmStreamMixer;
   }
   private void build3dScreenMenu() {
      if (this.itemSelected == 0 && this.spellSelected == 0) {
         this.menuActionNames[this.menuActionCount] = "Walk here";
         this.menuActionIds[this.menuActionCount] = 516;
         this.menuParam0[this.menuActionCount] = super.mouseX;
         this.menuParam1[this.menuActionCount] = super.mouseY;
         this.menuActionCount++;
      }

      int previousPickedId = -1;

      for (int pickedIdIndex = 0; pickedIdIndex < Model.pickedCount; pickedIdIndex++) {
         int pickedId;
         int tileX = (pickedId = Model.pickedIds[pickedIdIndex]) & 127;
         int tileY = pickedId >> 7 & 127;
         int entityType = pickedId >> 29 & 3;
         int entityId = pickedId >> 14 & 32767;
         if (pickedId != previousPickedId) {
            previousPickedId = pickedId;
            if (entityType == 2 && this.scene.getArrangement(this.plane, tileX, tileY, pickedId) >= 0) {
               ObjectDefinition objectDefinition;
               if ((objectDefinition = ObjectDefinition.lookup(entityId)).childIds != null) {
                  ObjectDefinition sourceObjectDefinition = objectDefinition;
                  int childIdIndex = -1;
                  if (sourceObjectDefinition.varbitId != -1) {
                     VarbitDefinition varbitDefinition;
                     int varpIndex = (varbitDefinition = VarbitDefinition.definitions[sourceObjectDefinition.varbitId]).index;
                     int leastSignificantBit = varbitDefinition.leastSignificantBit;
                     int mostSignificantBit = varbitDefinition.mostSignificantBit;
                     int bitMask = bitMasks[mostSignificantBit - leastSignificantBit];
                     childIdIndex = ObjectDefinition.clientInstance.varps[varpIndex] >> leastSignificantBit & bitMask;
                  } else if (sourceObjectDefinition.configId != -1) {
                     childIdIndex = ObjectDefinition.clientInstance.varps[sourceObjectDefinition.configId];
                  }

                  objectDefinition = childIdIndex >= 0 && childIdIndex < sourceObjectDefinition.childIds.length && sourceObjectDefinition.childIds[childIdIndex] != -1 ? ObjectDefinition.lookup(sourceObjectDefinition.childIds[childIdIndex]) : null;
               }

               if (objectDefinition == null) {
                  continue;
               }

               if (this.itemSelected == 1) {
                  this.menuActionNames[this.menuActionCount] = "Use " + this.selectedItemName + " with @cya@" + objectDefinition.name;
                  this.menuActionIds[this.menuActionCount] = 62;
                  this.menuParam2[this.menuActionCount] = pickedId;
                  this.menuParam0[this.menuActionCount] = tileX;
                  this.menuParam1[this.menuActionCount] = tileY;
                  this.menuActionCount++;
               } else if (this.spellSelected == 1) {
                  if ((this.spellUsableOn & 4) == 4) {
                     this.menuActionNames[this.menuActionCount] = this.spellTooltip + " @cya@" + objectDefinition.name;
                     this.menuActionIds[this.menuActionCount] = 956;
                     this.menuParam2[this.menuActionCount] = pickedId;
                     this.menuParam0[this.menuActionCount] = tileX;
                     this.menuParam1[this.menuActionCount] = tileY;
                     this.menuActionCount++;
                  }
               } else {
                  if (objectDefinition.actions != null) {
                     for (int actionIndex = 4; actionIndex >= 0; actionIndex--) {
                        if (objectDefinition.actions[actionIndex] != null) {
                           this.menuActionNames[this.menuActionCount] = objectDefinition.actions[actionIndex] + " @cya@" + objectDefinition.name;
                           if (actionIndex == 0) {
                              this.menuActionIds[this.menuActionCount] = 502;
                           }

                           if (actionIndex == 1) {
                              this.menuActionIds[this.menuActionCount] = 900;
                           }

                           if (actionIndex == 2) {
                              this.menuActionIds[this.menuActionCount] = 113;
                           }

                           if (actionIndex == 3) {
                              this.menuActionIds[this.menuActionCount] = 872;
                           }

                           if (actionIndex == 4) {
                              this.menuActionIds[this.menuActionCount] = 1062;
                           }

                           this.menuParam2[this.menuActionCount] = pickedId;
                           this.menuParam0[this.menuActionCount] = tileX;
                           this.menuParam1[this.menuActionCount] = tileY;
                           this.menuActionCount++;
                        }
                     }
                  }

                  this.menuActionNames[this.menuActionCount] = "Examine @cya@" + objectDefinition.name;
                  this.menuActionIds[this.menuActionCount] = 1226;
                  this.menuParam2[this.menuActionCount] = objectDefinition.type << 14;
                  this.menuParam0[this.menuActionCount] = tileX;
                  this.menuParam1[this.menuActionCount] = tileY;
                  this.menuActionCount++;
               }
            }

            if (entityType == 1) {
               Npc npc;
               if ((npc = this.npcs[entityId]).definition.size == 1 && (npc.worldX & 127) == 64 && (npc.worldY & 127) == 64) {
                  for (int npcIndex2 = 0; npcIndex2 < this.npcCount; npcIndex2++) {
                     Npc npc3;
                     if ((npc3 = this.npcs[this.npcIndices[npcIndex2]]) != null
                        && npc3 != npc
                        && npc3.definition.size == 1
                        && npc3.worldX == npc.worldX
                        && npc3.worldY == npc.worldY) {
                        this.buildAtNPCMenu(npc3.definition, this.npcIndices[npcIndex2], tileY, tileX);
                     }
                  }

                  for (int playerIndex = 0; playerIndex < this.playerCount; playerIndex++) {
                     Player player;
                     if ((player = this.players[this.playerIndices[playerIndex]]) != null && player.worldX == npc.worldX && player.worldY == npc.worldY) {
                        this.buildAtPlayerMenu(tileX, this.playerIndices[playerIndex], player, tileY);
                     }
                  }
               }

               this.buildAtNPCMenu(npc.definition, entityId, tileY, tileX);
            }

            if (entityType == 0) {
               Player player3;
               if (((player3 = this.players[entityId]).worldX & 127) == 64 && (player3.worldY & 127) == 64) {
                  for (int npcIndex3 = 0; npcIndex3 < this.npcCount; npcIndex3++) {
                     Npc npc2;
                     if ((npc2 = this.npcs[this.npcIndices[npcIndex3]]) != null
                        && npc2.definition.size == 1
                        && npc2.worldX == player3.worldX
                        && npc2.worldY == player3.worldY) {
                        this.buildAtNPCMenu(npc2.definition, this.npcIndices[npcIndex3], tileY, tileX);
                     }
                  }

                  for (int playerIndex2 = 0; playerIndex2 < this.playerCount; playerIndex2++) {
                     Player player2;
                     if ((player2 = this.players[this.playerIndices[playerIndex2]]) != null && player2 != player3 && player2.worldX == player3.worldX && player2.worldY == player3.worldY) {
                        this.buildAtPlayerMenu(tileX, this.playerIndices[playerIndex2], player2, tileY);
                     }
                  }
               }

               this.buildAtPlayerMenu(tileX, entityId, player3, tileY);
            }

            NodeDeque nodeDeque;
            if (entityType == 3 && (nodeDeque = this.groundItems[this.plane][tileX][tileY]) != null) {
               for (GroundItem groundItem = (GroundItem)nodeDeque.last(); groundItem != null; groundItem = (GroundItem)nodeDeque.previous()) {
                  ItemDefinition itemDefinition;
                  if ((itemDefinition = ItemDefinition.lookup(groundItem.id)) != null) {
                     String text = "@lre@";
                     boolean flag = false;
                     if (groundItemOtherMenuColorEnabled && groundItemOtherNames.size() > 0 && itemDefinition.name != null) {
                        Iterator iterator = groundItemOtherNames.iterator();

                        while (iterator.hasNext()) {
                           String localText;
                           if ((localText = (String)iterator.next()) != null && localText.toLowerCase().equals(itemDefinition.name.toLowerCase())) {
                              text = "@ud1@";
                              flag = true;
                              break;
                           }
                        }
                     }

                     if (!flag && groundItemRareMenuColorEnabled && groundItemRareNames.size() > 0 && itemDefinition.name != null) {
                        Iterator iterator2 = groundItemRareNames.iterator();

                        while (iterator2.hasNext()) {
                           String text2;
                           if ((text2 = (String)iterator2.next()) != null && text2.toLowerCase().equals(itemDefinition.name.toLowerCase())) {
                              text = "@ud2@";
                              break;
                           }
                        }
                     }

                     if (this.itemSelected == 1) {
                        this.menuActionNames[this.menuActionCount] = "Use " + this.selectedItemName + " with @lre@" + itemDefinition.name;
                        this.menuActionIds[this.menuActionCount] = 511;
                        this.menuParam2[this.menuActionCount] = groundItem.id;
                        this.menuParam0[this.menuActionCount] = tileX;
                        this.menuParam1[this.menuActionCount] = tileY;
                        this.menuActionCount++;
                     } else if (this.spellSelected == 1) {
                        if ((this.spellUsableOn & 1) == 1) {
                           this.menuActionNames[this.menuActionCount] = this.spellTooltip + " @lre@" + itemDefinition.name;
                           this.menuActionIds[this.menuActionCount] = 94;
                           this.menuParam2[this.menuActionCount] = groundItem.id;
                           this.menuParam0[this.menuActionCount] = tileX;
                           this.menuParam1[this.menuActionCount] = tileY;
                           this.menuActionCount++;
                        }
                     } else {
                        for (int groundOptionIndex = 4; groundOptionIndex >= 0; groundOptionIndex--) {
                           if (itemDefinition.groundOptions != null && itemDefinition.groundOptions[groundOptionIndex] != null) {
                              this.menuActionNames[this.menuActionCount] = itemDefinition.groundOptions[groundOptionIndex] + " @lre@" + itemDefinition.name;
                              if (groundOptionIndex == 0) {
                                 this.menuActionIds[this.menuActionCount] = 652;
                              }

                              if (groundOptionIndex == 1) {
                                 this.menuActionIds[this.menuActionCount] = 567;
                              }

                              if (groundOptionIndex == 2) {
                                 this.menuActionIds[this.menuActionCount] = 234;
                              }

                              if (groundOptionIndex == 3) {
                                 this.menuActionIds[this.menuActionCount] = 244;
                              }

                              if (groundOptionIndex == 4) {
                                 this.menuActionIds[this.menuActionCount] = 213;
                              }

                              this.menuParam2[this.menuActionCount] = groundItem.id;
                              this.menuParam0[this.menuActionCount] = tileX;
                              this.menuParam1[this.menuActionCount] = tileY;
                              this.menuActionCount++;
                           } else if (groundOptionIndex == 2) {
                              this.menuActionNames[this.menuActionCount] = "Take " + text + itemDefinition.name;
                              this.menuActionIds[this.menuActionCount] = 234;
                              this.menuParam2[this.menuActionCount] = groundItem.id;
                              this.menuParam0[this.menuActionCount] = tileX;
                              this.menuParam1[this.menuActionCount] = tileY;
                              this.menuActionCount++;
                           }
                        }

                        this.menuActionNames[this.menuActionCount] = "Examine " + text + itemDefinition.name;
                        this.menuActionIds[this.menuActionCount] = 1448;
                        this.menuParam2[this.menuActionCount] = groundItem.id;
                        this.menuParam0[this.menuActionCount] = tileX;
                        this.menuParam1[this.menuActionCount] = tileY;
                        this.menuActionCount++;
                     }
                  }
               }
            }
         }
      }
   }
   @Override
   public final void cleanUpForQuit() {
      SignLink.unusedPublicFlag = false;

      try {
         if (this.connection != null) {
            this.connection.close();
         }
      } catch (Exception exception) {
      }

      this.connection = null;
      signalMidiStop();
      if (this.mouseRecorder != null) {
         this.mouseRecorder.running = false;
      }

      this.mouseRecorder = null;
      this.onDemandFetcher.disable();
      this.onDemandFetcher = null;
      this.chatBuffer = null;
      this.outgoingBuffer = null;
      this.loginBuffer = null;
      this.inStream = null;
      this.regionIds = null;
      this.terrainRegionData = null;
      this.objectRegionData = null;
      this.terrainArchiveIds = null;
      this.objectArchiveIds = null;
      this.intGroundArray = null;
      this.byteGroundArray = null;
      this.scene = null;
      this.collisionMaps = null;
      this.pathDirections = null;
      this.pathDistances = null;
      this.bigX = null;
      this.bigY = null;
      this.animatedTextureScratch = null;
      this.tabImageProducer = null;
      this.minimapImageProducer = null;
      this.gameScreenImageProducer = null;
      this.chatboxImageProducer = null;
      this.bottomFrameStripBuffer = null;
      this.bottomRightFrameStripBuffer = null;
      this.topRightFrameStripBuffer = null;
      this.rightFrameStripBuffer = null;
      this.middleRightFrameStripBuffer = null;
      this.chatRightFrameStripBuffer = null;
      this.chatTopFrameStripBuffer = null;
      this.chatLeftFrameStripBuffer = null;
      this.minimapRightFrameStripBuffer = null;
      this.minimapLeftFrameStripBuffer = null;
      this.chatSettingImageProducer = null;
      this.backVmidIP2_2 = null;
      this.invBack = null;
      mapBack = null;
      mapBackSprites = null;
      this.chatBack = null;
      this.backBase1 = null;
      this.backBase2 = null;
      this.backHmid1 = null;
      this.sideIcons = null;
      this.redStone1 = null;
      this.redStone2 = null;
      this.redStone3 = null;
      this.redStone1_2 = null;
      this.redStone2_2 = null;
      this.redStone1_3 = null;
      this.redStone2_3 = null;
      this.redStone3_2 = null;
      this.redStone1_4 = null;
      this.redStone2_4 = null;
      compassSprite = null;
      defaultCompassSprite = null;
      this.hitMarks = null;
      this.headIcons = null;
      this.skullIcons = null;
      this.headIconsHint = null;
      miscInterfaceSprites = null;
      this.crosses = null;
      this.skillIconSprites = null;
      this.mapDotItem = null;
      this.mapDotNpc = null;
      this.mapDotNPC = null;
      this.mapDotPlayer = null;
      this.mapDotFriend = null;
      this.mapDotTeam = null;
      this.mapSceneSprites = null;
      this.mapFunctions = null;
      this.tileCycleMarkers = null;
      this.players = null;
      this.playerIndices = null;
      this.entityUpdateIndices = null;
      this.playerAppearanceBuffers = null;
      this.removedEntityIndices = null;
      this.npcs = null;
      this.npcIndices = null;
      this.groundItems = null;
      this.spawns = null;
      this.projectiles = null;
      this.incompleteAnimables = null;
      this.menuParam0 = null;
      this.menuParam1 = null;
      this.menuActionIds = null;
      this.menuParam2 = null;
      this.menuActionNames = null;
      this.varps = null;
      this.minimapHintX = null;
      this.minimapHintY = null;
      this.minimapHint = null;
      this.minimapImage = null;
      this.friendNames = null;
      this.friendEncodedNames = null;
      this.friendWorlds = null;
      this.flameRightBackground = null;
      this.bottomLeft0BackgroundTile = null;
      this.topLeft1BackgroundTile = null;
      this.bottomLeft1BackgroundTile = null;
      this.flameLeftBackground = null;
      this.bottomRightImageProducer = null;
      this.loginMusicImageProducer = null;
      this.middleLeft1BackgroundTile = null;
      this.middleRightBackgroundBuffer = null;
      this.multiOverlay = null;
      this.unloadTitleScreen();
      ObjectDefinition.nullLoader();
      NpcDefinition.nullLoader();
      ItemDefinition.clearCache();
      FloorDefinition.definitions = null;
      IdentityKit.kits = null;
      Widget.widgets = null;
      AnimationSequence.sequences = null;
      SpotAnimationDefinition.definitions = null;
      SpotAnimationDefinition.modelCache = null;
      VarpDefinition.definitions = null;
      super.graphicsBuffer = null;
      Player.appearanceModelCache = null;
      Rasterizer3D.clear();
      SceneGraph.nullLoader();
      Model.clearModelLoader();
      AnimationFrame.clear();
      System.gc();
   }
   static final int greatestCommonDivisor(int scalarArgument, int scalarArgument2) {
      if (scalarArgument > scalarArgument2) {
         int scalar = scalarArgument2;
         scalarArgument2 = scalarArgument;
         scalarArgument = scalar;
      }

      while (scalarArgument != 0) {
         int scalar2 = scalarArgument2 % scalarArgument;
         scalarArgument2 = scalarArgument;
         scalarArgument = scalar2;
      }

      return scalarArgument2;
   }
   @Override
   final Component getGameComponent() {
      return super.gameFrame != null ? super.gameFrame : this;
   }
   private void selectBankTab(int newSelectedBankTab, boolean flag) {
      selectedBankTab = newSelectedBankTab;

      for (int bankTabIndex = 0; bankTabIndex < bankTabs.length; bankTabIndex++) {
         BankTab bankTab = bankTabs[bankTabIndex];
         if (bankTabIndex == selectedBankTab) {
            Widget.widgets[5385].childX[bankTab.tabIndex] = 38;
            Widget.widgets[5385].childY[bankTab.tabIndex] = 3;
            Widget.widgets[5292].childX[120] = 56 + bankTabIndex * 40;
         } else {
            Widget.widgets[5385].childX[bankTab.tabIndex] = 65000;
            Widget.widgets[5385].childY[bankTab.tabIndex] = 3;
         }
      }

      Widget.widgets[5385].scrollPosition = 0;
      if (!flag) {
         this.layoutBankTabs();
      }
   }
   private void layoutBankTabs() {
      int sourceScrollMax = 3;
      int scalar = 3;
      int sourceTabLayoutUnits = 0;
      if (selectedBankTab == 0) {
         for (int bankTabIndex2 = 0; bankTabIndex2 < getBankTabCount(); bankTabIndex2++) {
            int childXOrGetBankTabCount = 38;
            int tabLayoutUnits = 0;
            BankTab bankTab = bankTabs[bankTabIndex2];
            if (bankTabIndex2 > 0) {
               if (fetchMusic && (!this.bankSearchHasMatches || server == "" || bankTabIndex2 > this.bankSearchLastMatchingTab && this.bankSearchLastMatchingTab != -1)) {
                  childXOrGetBankTabCount = 65000;
               }

               Widget.widgets[5385].childX[bankTabIndex2] = childXOrGetBankTabCount;
               Widget.widgets[5385].childY[bankTabIndex2] = sourceScrollMax;
               sourceScrollMax += 7;
               if (fetchMusic && !bankTab.hasSearchResults()) {
                  childXOrGetBankTabCount = 65000;
                  tabLayoutUnits = -15;
               }

               Widget.widgets[5385].childX[bankTabIndex2 + 18] = childXOrGetBankTabCount;
               Widget.widgets[5385].childY[bankTabIndex2 + 18] = sourceScrollMax;
               sourceScrollMax += tabLayoutUnits + 15;
            }

            if (fetchMusic && bankTab.hasSearchResults() || !fetchMusic) {
               childXOrGetBankTabCount = getBankTabCount() - 1;
               if (fetchMusic && this.bankSearchLastMatchingTab != -1) {
                  childXOrGetBankTabCount = this.bankSearchLastMatchingTab;
               }

               if (bankTabIndex2 == childXOrGetBankTabCount) {
                  scalar = sourceScrollMax;
               }

               if (bankTabIndex2 > 0) {
                  Widget.widgets[5385].childX[bankTab.tabIndex] = 38;
                  Widget.widgets[5385].childY[bankTab.tabIndex] = sourceScrollMax;
               }

               tabLayoutUnits = (tabLayoutUnits = bankTab.getDisplayedItemCount()) % 8 == 0 ? tabLayoutUnits / 8 : tabLayoutUnits / 8 + 1;
               if (bankTabIndex2 == childXOrGetBankTabCount) {
                  sourceTabLayoutUnits = tabLayoutUnits;
               }

               if (tabLayoutUnits != 0 || bankTabIndex2 == 0) {
                  sourceScrollMax += tabLayoutUnits * 36;
               }
            }
         }

         sourceScrollMax = scalar + sourceTabLayoutUnits * 36;

         for (int childXIndex = 1; childXIndex < bankTabs.length; childXIndex++) {
            if (isBankTabEmpty(childXIndex)) {
               Widget.widgets[5385].childX[childXIndex] = 65000;
               Widget.widgets[5385].childY[childXIndex] = 40;
               Widget.widgets[5385].childX[childXIndex + 18] = 65000;
               Widget.widgets[5385].childY[childXIndex + 18] = 40;
            }
         }
      }

      if (selectedBankTab != 0 && getBankTabCount() > 1) {
         for (int bankTabIndex = 0; bankTabIndex < bankTabs.length - 1; bankTabIndex++) {
            BankTab bankTab2;
            int displayedItemCount = (bankTab2 = bankTabs[bankTabIndex]).getDisplayedItemCount();
            Widget.widgets[5385].childX[bankTabIndex + 1] = 65000;
            Widget.widgets[5385].childY[bankTabIndex + 1] = 40;
            Widget.widgets[5385].childX[bankTabIndex + 19] = 65000;
            Widget.widgets[5385].childY[bankTabIndex + 19] = 40;
            if (bankTabIndex == selectedBankTab) {
               int scalar2 = displayedItemCount % 8 == 0 ? displayedItemCount / 8 : displayedItemCount / 8 + 1;
               sourceScrollMax = scalar + scalar2 * 36;
            }
         }
      }

      Widget.widgets[5385].scrollMax = sourceScrollMax;
   }
   private void updateBankTabs() {
      for (int bankTabIndex2 = 0; bankTabIndex2 < bankTabs.length; bankTabIndex2++) {
         BankTab bankTab = bankTabs[bankTabIndex2];
         if (bankTabIndex2 < getBankTabCount() + 1) {
            Widget.widgets[bankTab.toggleWidgetId].hoverOnly = false;
         } else {
            Widget.widgets[bankTab.toggleWidgetId].hoverOnly = true;
         }

         if (bankTabIndex2 > 0) {
            String text = "View tab @lre@" + bankTabIndex2 + "@whi@";
            if (bankTabIndex2 == getBankTabCount()) {
               text = "New tab";
            }

            Widget.widgets[bankTab.actionWidgetId].tooltip = text;
         }
      }

      Widget.widgets[5292].childX[122] = 108 + (getBankTabCount() < 10 ? (getBankTabCount() - 1) * 40 : 65000);

      for (int inventoryIdIndex = 1; inventoryIdIndex < bankTabs.length; inventoryIdIndex++) {
         int localFindFirstBankTabItemSlot = findFirstBankTabItemSlot(inventoryIdIndex);
         Widget.widgets[bankTabSummaryWidgetId].inventoryIds[inventoryIdIndex] = bankTabItemIds[inventoryIdIndex][localFindFirstBankTabItemSlot];
         Widget.widgets[bankTabSummaryWidgetId].inventoryAmounts[inventoryIdIndex] = this.bankTabItemAmounts[inventoryIdIndex][localFindFirstBankTabItemSlot];
      }

      int bankTabIndex = selectedBankTab;
      if (isBankTabEmpty(selectedBankTab) && selectedBankTab > 0) {
         this.selectBankTab(--bankTabIndex, true);
         BankTab bankTab2 = bankTabs[bankTabIndex];
         this.outgoingBuffer.writeOpcode(185);
         this.outgoingBuffer.writeShort(bankTab2.actionWidgetId);
      }

      this.layoutBankTabs();
   }
   private static int findFirstBankTabItemSlot(int bankTabIndex) {
      BankTab bankTab = bankTabs[bankTabIndex];

      for (int itemIndex = 0; itemIndex < bankTab.itemCount; itemIndex++) {
         if (bankTabItemIds[bankTabIndex][itemIndex] != 0) {
            return itemIndex;
         }
      }

      return 0;
   }
   private static boolean isBankTabEmpty(int bankTabIndex) {
      BankTab bankTab = bankTabs[bankTabIndex];

      for (int itemIndex = 0; itemIndex < bankTab.itemCount; itemIndex++) {
         if (bankTabItemIds[bankTabIndex][itemIndex] != 0) {
            return false;
         }
      }

      return true;
   }
   private void updateBankSearch() {
      for (int bankTabIndex2 = 0; bankTabIndex2 < bankTabs.length; bankTabIndex2++) {
         BankTab bankTab;
         (bankTab = bankTabs[bankTabIndex2]).searchResultSlots.clear();
      }

      if (this.inputDialogState == 2) {
         Client client = this;
         if (server != null) {
            client.bankSearchHasMatches = false;
            client.bankSearchLastMatchingTab = -1;

            for (int bankTabIndex = selectedBankTab; bankTabIndex < (selectedBankTab == 0 ? bankTabs.length : selectedBankTab + 1); bankTabIndex++) {
               BankTab bankTab3;
               (bankTab3 = bankTabs[bankTabIndex]).hasSearchResults = false;
               int[] bankTabItemId = bankTabItemIds[bankTabIndex];
               int[] bankTabItemAmount = client.bankTabItemAmounts[bankTabIndex];
               ArrayList arrayList = new ArrayList();
               ArrayList arrayList2 = new ArrayList();

               for (int bankTabItemIdIndex = 0; bankTabItemIdIndex < bankTabItemId.length; bankTabItemIdIndex++) {
                  int scalar = bankTabItemId[bankTabItemIdIndex] - 1;
                  String text;
                  if (bankTabItemId[bankTabItemIdIndex] > 0 && (text = ItemDefinition.lookup(scalar).name) != null && text.toLowerCase().contains(server.toLowerCase())) {
                     arrayList.add(bankTabItemId[bankTabItemIdIndex]);
                     arrayList2.add(bankTabItemAmount[bankTabItemIdIndex]);
                     bankTab3.searchResultSlots.add(bankTabItemIdIndex);
                     bankTab3.hasSearchResults = true;
                     client.bankSearchLastMatchingTab = bankTabIndex;
                     if (!client.bankSearchHasMatches) {
                        client.bankSearchHasMatches = true;
                     }
                  }
               }

               Widget.widgets[bankTab3.containerWidgetId].inventoryIds = new int[bankTabItemId.length];
               Widget.widgets[bankTab3.containerWidgetId].inventoryAmounts = new int[bankTabItemAmount.length];

               for (int inventoryIdIndex = 0; inventoryIdIndex < arrayList.size(); inventoryIdIndex++) {
                  if (server == "") {
                     Widget.widgets[bankTab3.containerWidgetId].inventoryIds[inventoryIdIndex] = 0;
                     Widget.widgets[bankTab3.containerWidgetId].inventoryAmounts[inventoryIdIndex] = 0;
                  } else {
                     Widget.widgets[bankTab3.containerWidgetId].inventoryIds[inventoryIdIndex] = (Integer)arrayList.get(inventoryIdIndex);
                     Widget.widgets[bankTab3.containerWidgetId].inventoryAmounts[inventoryIdIndex] = (Integer)arrayList2.get(inventoryIdIndex);
                  }
               }

               bankTab3.filteredItemCount = arrayList.size();
            }

            Widget.widgets[5383].message = "Bank of RuneScape (search: '" + server + "')";
            if (server == "") {
               Widget.widgets[5383].message = "Bank of RuneScape (no search entered)";
               Widget.widgets[18787].message = "No search term entered!";
            } else if (!client.bankSearchHasMatches) {
               Widget.widgets[18787].message = "No matches found!";
            } else {
               Widget.widgets[18787].message = "";
            }
         }

         fetchMusic = true;
      } else {
         fetchMusic = false;
         Widget.widgets[18787].message = "";

         for (int bankTabIndex3 = 0; bankTabIndex3 < bankTabs.length; bankTabIndex3++) {
            BankTab bankTab2 = bankTabs[bankTabIndex3];
            Widget.widgets[bankTab2.containerWidgetId].inventoryIds = bankTabItemIds[bankTabIndex3];
            Widget.widgets[bankTab2.containerWidgetId].inventoryAmounts = this.bankTabItemAmounts[bankTabIndex3];
         }

         Widget.widgets[5383].message = this.bankTitle;
         if (this.savedBankScrollPosition != 0) {
            Widget.widgets[5385].scrollPosition = this.savedBankScrollPosition;
            this.savedBankScrollPosition = 0;
         }
      }

      this.updateBankTabs();
   }
   private void sendTeleportCommand(int scalarArgument, int scalarArgument2) {
      String text = "::tele " + scalarArgument + " " + scalarArgument2;
      this.outgoingBuffer.writeOpcode(103);
      this.outgoingBuffer.writeByte(text.length() - 1);
      this.outgoingBuffer.writeString(text.substring(2));
   }
   private void manageTextInputs() {
      int currentPosition;
      while ((currentPosition = this.readChar(-796)) != -1) {
         if (this.openInterfaceId != -1 && this.openInterfaceId == this.reportAbuseInterfaceID) {
            if (currentPosition == 8 && this.reportAbuseInput.length() > 0) {
               this.reportAbuseInput = this.reportAbuseInput.substring(0, this.reportAbuseInput.length() - 1);
            }

            if ((currentPosition >= 97 && currentPosition <= 122 || currentPosition >= 65 && currentPosition <= 90 || currentPosition >= 48 && currentPosition <= 57 || currentPosition == 32) && this.reportAbuseInput.length() < 12) {
               this.reportAbuseInput = this.reportAbuseInput + (char)currentPosition;
            }
         } else if (this.messagePromptRaised) {
            if (currentPosition >= 32 && currentPosition <= 122 && this.promptInput.length() < 80) {
               this.promptInput = this.promptInput + (char)currentPosition;
               this.inputTaken = true;
            }

            if (currentPosition == 8 && this.promptInput.length() > 0) {
               this.promptInput = this.promptInput.substring(0, this.promptInput.length() - 1);
               this.inputTaken = true;
            }

            if (currentPosition == 13 || currentPosition == 10) {
               this.messagePromptRaised = false;
               this.inputTaken = true;
               if (this.friendsListAction == 1) {
                  long encodedName = NameUtils.encodeBase37(this.promptInput);
                  this.addFriend(encodedName);
               }

               if (this.friendsListAction == 2 && this.friendCount > 0) {
                  long encodeBase372 = NameUtils.encodeBase37(this.promptInput);
                  this.removeFriend(encodeBase372);
               }

               if (this.friendsListAction == 3 && this.promptInput.length() > 0) {
                  this.outgoingBuffer.writeOpcode(126);
                  this.outgoingBuffer.writeByte(0);
                  currentPosition = this.outgoingBuffer.currentPosition;
                  this.outgoingBuffer.writeLong(this.privateMessageTarget);
                  ChatCodec.encode(this.promptInput, this.outgoingBuffer);
                  this.outgoingBuffer.writeLengthByte(this.outgoingBuffer.currentPosition - currentPosition);
                  this.promptInput = ChatCodec.normalize(this.promptInput);
                  this.promptInput = ChatFilter.apply(this.promptInput);
                  this.pushMessage(this.promptInput, 6, NameUtils.formatDisplayName(NameUtils.decodeBase37(this.privateMessageTarget)), 0, 0, 0);
                  if (this.privateChatMode == 2) {
                     this.privateChatMode = 1;
                     this.chatSettingsRedraw = true;
                     this.outgoingBuffer.writeOpcode(95);
                     this.outgoingBuffer.writeByte(this.publicChatMode);
                     this.outgoingBuffer.writeByte(this.privateChatMode);
                     this.outgoingBuffer.writeByte(this.tradeMode);
                  }
               }

               if (this.friendsListAction == 4 && this.ignoreCount < 100) {
                  long encodeBase373 = NameUtils.encodeBase37(this.promptInput);
                  this.addIgnore(encodeBase373);
               }

               if (this.friendsListAction == 5 && this.ignoreCount > 0) {
                  long encodeBase374 = NameUtils.encodeBase37(this.promptInput);
                  this.removeIgnore(encodeBase374);
               }
            }
         } else if (this.inputDialogState == 1) {
            if (currentPosition >= 48 && currentPosition <= 57 && this.amountOrNameInput.length() < 10) {
               this.amountOrNameInput = this.amountOrNameInput + (char)currentPosition;
               this.inputTaken = true;
            }

            if (!this.amountOrNameInput.toLowerCase().contains("k")
                  && !this.amountOrNameInput.toLowerCase().contains("m")
                  && !this.amountOrNameInput.toLowerCase().contains("b")
                  && (currentPosition == 107 || currentPosition == 109)
               || currentPosition == 98) {
               this.amountOrNameInput = this.amountOrNameInput + (char)currentPosition;
               this.inputTaken = true;
            }

            if (currentPosition == 8 && this.amountOrNameInput.length() > 0) {
               this.amountOrNameInput = this.amountOrNameInput.substring(0, this.amountOrNameInput.length() - 1);
               this.inputTaken = true;
            }

            if (currentPosition == 13 || currentPosition == 10) {
               if (this.amountOrNameInput.length() > 0) {
                  if (this.amountOrNameInput.toLowerCase().contains("k")) {
                     this.amountOrNameInput = this.amountOrNameInput.replaceAll("k", "000");
                  } else if (this.amountOrNameInput.toLowerCase().contains("m")) {
                     this.amountOrNameInput = this.amountOrNameInput.replaceAll("m", "000000");
                  } else if (this.amountOrNameInput.toLowerCase().contains("b")) {
                     this.amountOrNameInput = this.amountOrNameInput.replaceAll("b", "000000000");
                  }

                  long localParseLong = 0L;

                  try {
                     localParseLong = Long.parseLong(this.amountOrNameInput);
                  } catch (Exception exception) {
                  }

                  currentPosition = localParseLong > 2147483647L ? Integer.MAX_VALUE : (int)localParseLong;
                  this.outgoingBuffer.writeOpcode(208);
                  this.outgoingBuffer.writeInt(currentPosition);
               }

               this.inputDialogState = 0;
               this.inputTaken = true;
            }
         } else if (this.inputDialogState == 2) {
            if (currentPosition >= 32 && currentPosition <= 122 && this.amountOrNameInput.length() < 12) {
               this.amountOrNameInput = this.amountOrNameInput + (char)currentPosition;
               this.inputTaken = true;
            }

            if (currentPosition == 8 && this.amountOrNameInput.length() > 0) {
               this.amountOrNameInput = this.amountOrNameInput.substring(0, this.amountOrNameInput.length() - 1);
               this.inputTaken = true;
            }

            if (currentPosition == 13 || currentPosition == 10) {
               if (this.amountOrNameInput.length() > 0) {
                  this.outgoingBuffer.writeOpcode(60);
                  this.outgoingBuffer.writeLong(NameUtils.encodeBase37(this.amountOrNameInput));
               }

               this.inputDialogState = 0;
               this.inputTaken = true;
            }

            if (this.openInterfaceId == 5292) {
               if (this.amountOrNameInput.length() <= 0) {
                  server = "";
               } else {
                  server = this.amountOrNameInput;
               }

               this.updateBankSearch();
            }
         } else if (this.inputDialogState == 3) {
            if (currentPosition == 10) {
               this.inputDialogState = 0;
               this.inputTaken = true;
            }

            if (currentPosition >= 32 && currentPosition <= 122 && this.amountOrNameInput.length() < 40) {
               this.amountOrNameInput = this.amountOrNameInput + (char)currentPosition;
               this.inputTaken = true;
            }

            if (currentPosition == 8 && this.amountOrNameInput.length() > 0) {
               this.amountOrNameInput = this.amountOrNameInput.substring(0, this.amountOrNameInput.length() - 1);
               this.inputTaken = true;
            }
         } else if (this.backDialogID == -1) {
            if (currentPosition >= 32 && currentPosition <= 122 && this.inputString.length() < 70) {
               this.inputString = this.inputString + (char)currentPosition;
               this.inputTaken = true;
            }

            if (currentPosition == 8 && this.inputString.length() > 0) {
               this.inputString = this.inputString.substring(0, this.inputString.length() - 1);
               this.inputTaken = true;
            }

            if (currentPosition == 9) {
               this.replyToLastPrivateMessage();
            }

            if ((currentPosition == 13 || currentPosition == 10) && this.inputString.length() > 0) {
               if (this.inputString.startsWith("::item")) {
                  this.amountOrNameInput = "";
                  this.clanChatMode = 0;
                  itemSearchSpawnMode = true;
                  this.inputDialogState = 3;
                  this.itemSpawnAmount = 1;

                  try {
                     int itemSpawnAmountOrParseInt;
                     if ((itemSpawnAmountOrParseInt = Integer.parseInt(this.inputString.split(" ")[1])) > 0) {
                        this.itemSpawnAmount = itemSpawnAmountOrParseInt;
                     }
                  } catch (Exception exception2) {
                  }
               }

               if (this.inputString.equals("::lvls")) {
                  Widget.enableSkillLevelActions(true);
                  this.pushMessage("You can now set ur skill levels.", 0, "", 0, 0, 0);
               }

               if (this.inputString.equals("::fps")) {
                  fpsOn = !fpsOn;
               }

               if (this.inputString.startsWith("/")) {
                  this.outgoingBuffer.writeOpcode(103);
                  String text = "yell " + this.inputString.substring(1);
                  this.outgoingBuffer.writeByte(text.length() + 1);
                  this.outgoingBuffer.writeString(text);
               } else if (this.inputString.startsWith("::")) {
                  this.outgoingBuffer.writeOpcode(103);
                  this.outgoingBuffer.writeByte(this.inputString.length() - 1);
                  this.outgoingBuffer.writeString(this.inputString.substring(2));
               } else {
                  String localText = this.inputString.toLowerCase();
                  byte sourceTextColor = 0;
                  if (localText.startsWith("yellow:")) {
                     sourceTextColor = 0;
                     this.inputString = this.inputString.substring(7);
                  } else if (localText.startsWith("red:")) {
                     sourceTextColor = 1;
                     this.inputString = this.inputString.substring(4);
                  } else if (localText.startsWith("green:")) {
                     sourceTextColor = 2;
                     this.inputString = this.inputString.substring(6);
                  } else if (localText.startsWith("cyan:")) {
                     sourceTextColor = 3;
                     this.inputString = this.inputString.substring(5);
                  } else if (localText.startsWith("purple:")) {
                     sourceTextColor = 4;
                     this.inputString = this.inputString.substring(7);
                  } else if (localText.startsWith("white:")) {
                     sourceTextColor = 5;
                     this.inputString = this.inputString.substring(6);
                  } else if (localText.startsWith("flash1:")) {
                     sourceTextColor = 6;
                     this.inputString = this.inputString.substring(7);
                  } else if (localText.startsWith("flash2:")) {
                     sourceTextColor = 7;
                     this.inputString = this.inputString.substring(7);
                  } else if (localText.startsWith("flash3:")) {
                     sourceTextColor = 8;
                     this.inputString = this.inputString.substring(7);
                  } else if (localText.startsWith("glow1:")) {
                     sourceTextColor = 9;
                     this.inputString = this.inputString.substring(6);
                  } else if (localText.startsWith("glow2:")) {
                     sourceTextColor = 10;
                     this.inputString = this.inputString.substring(6);
                  } else if (localText.startsWith("glow3:")) {
                     sourceTextColor = 11;
                     this.inputString = this.inputString.substring(6);
                  }

                  String text2 = this.inputString.toLowerCase();
                  byte sourceTextEffect = 0;
                  if (text2.startsWith("wave:")) {
                     sourceTextEffect = 1;
                     this.inputString = this.inputString.substring(5);
                  } else if (text2.startsWith("wave2:")) {
                     sourceTextEffect = 2;
                     this.inputString = this.inputString.substring(6);
                  } else if (text2.startsWith("shake:")) {
                     sourceTextEffect = 3;
                     this.inputString = this.inputString.substring(6);
                  } else if (text2.startsWith("scroll:")) {
                     sourceTextEffect = 4;
                     this.inputString = this.inputString.substring(7);
                  } else if (text2.startsWith("slide:")) {
                     sourceTextEffect = 5;
                     this.inputString = this.inputString.substring(6);
                  }

                  this.outgoingBuffer.writeOpcode(4);
                  this.outgoingBuffer.writeByte(0);
                  currentPosition = this.outgoingBuffer.currentPosition;
                  this.outgoingBuffer.writeByteSubtracted(sourceTextEffect);
                  this.outgoingBuffer.writeByteSubtracted(sourceTextColor);
                  this.chatBuffer.currentPosition = 0;
                  ChatCodec.encode(this.inputString, this.chatBuffer);
                  this.outgoingBuffer.writeBytesReversedAdded(0, this.chatBuffer.buffer, this.chatBuffer.currentPosition);
                  this.outgoingBuffer.writeLengthByte(this.outgoingBuffer.currentPosition - currentPosition);
                  this.inputString = ChatCodec.normalize(this.inputString);
                  this.inputString = ChatFilter.apply(this.inputString);
                  localPlayer.spokenText = this.inputString;
                  localPlayer.textColor = sourceTextColor;
                  localPlayer.textEffect = sourceTextEffect;
                  localPlayer.textCycle = 150;
                  if (this.yCameraCurve != 2 && this.yCameraCurve != 3) {
                     this.pushMessage(localPlayer.spokenText, 2, localPlayer.name, this.yCameraCurve, this.myPrivilege, this.xCameraCurve);
                  } else {
                     this.pushMessage(localPlayer.spokenText, 2, localPlayer.name, this.yCameraCurve, 0, 0);
                  }

                  if (this.publicChatMode == 2) {
                     this.publicChatMode = 3;
                     this.chatSettingsRedraw = true;
                     this.outgoingBuffer.writeOpcode(95);
                     this.outgoingBuffer.writeByte(this.publicChatMode);
                     this.outgoingBuffer.writeByte(this.privateChatMode);
                     this.outgoingBuffer.writeByte(this.tradeMode);
                  }
               }

               this.inputString = "";
               this.inputTaken = true;
            }
         }
      }
   }
   private void buildPublicChatMenu(int scalarArgument) {
      int scalar = 0;
      int scalar2 = -3;

      for (int chatMessageIndex = 0; chatMessageIndex < 100; chatMessageIndex++) {
         if (this.chatMessages[chatMessageIndex] != null && chatViewMode == 1) {
            int chatType = this.chatTypes[chatMessageIndex];
            String text;
            String text2 = text = this.chatNames[chatMessageIndex];
            text = buildChatNameIcons(this.chatPrivileges[chatMessageIndex], this.chatDonatorStatuses[chatMessageIndex], this.chatAccountModes[chatMessageIndex]) + text;
            int scalar3 = 70 - scalar * 14 + this.chatScrollOffset + 4;
            if (gameframeVersion == 474 && screenMode != 0) {
               scalar3 = 70 - scalar2 * 14 + this.chatScrollOffset + 4 - 2;
            }

            if (scalar3 < -20) {
               break;
            }

            if ((chatType == 1 || chatType == 2 || chatType == 98) && (chatType == 1 || this.publicChatMode == 0 || this.publicChatMode == 1 && this.isFriendOrSelf(text2))) {
               if (scalarArgument > scalar3 - 14 && scalarArgument <= scalar3 && !text2.equals(localPlayer.name)) {
                  this.menuActionNames[this.menuActionCount] = "Report abuse @whi@" + text;
                  this.menuActionIds[this.menuActionCount] = 606;
                  this.menuActionCount++;
                  this.menuActionNames[this.menuActionCount] = "Add ignore @whi@" + text;
                  this.menuActionIds[this.menuActionCount] = 42;
                  this.menuActionCount++;
                  this.menuActionNames[this.menuActionCount] = "Add friend @whi@" + text;
                  this.menuActionIds[this.menuActionCount] = 337;
                  this.menuActionCount++;
                  this.menuActionNames[this.menuActionCount] = "Group Inv/Kick @whi@" + text;
                  this.menuActionIds[this.menuActionCount] = 1999;
                  this.menuActionCount++;
               }

               scalar++;
               scalar2++;
            }
         }
      }
   }
   private void buildPrivateChatMenu(int scalarArgument) {
      int scalar = 0;
      int scalar2 = -3;

      for (int chatMessageIndex = 0; chatMessageIndex < 100; chatMessageIndex++) {
         if (this.chatMessages[chatMessageIndex] != null && chatViewMode == 2) {
            int chatType = this.chatTypes[chatMessageIndex];
            String text;
            String text2 = text = this.chatNames[chatMessageIndex];
            text = buildChatNameIcons(this.chatPrivileges[chatMessageIndex], this.chatDonatorStatuses[chatMessageIndex], this.chatAccountModes[chatMessageIndex]) + text;
            int scalar3 = 70 - scalar * 14 + this.chatScrollOffset + 4;
            if (gameframeVersion == 474 && screenMode != 0) {
               scalar3 = 70 - scalar2 * 14 + this.chatScrollOffset + 4 - 2;
            }

            if (scalar3 < -20) {
               break;
            }

            if ((chatType == 5 || chatType == 6)
               && (this.splitpublicChat == 0 || chatViewMode == 2)
               && (chatType == 6 || this.privateChatMode == 0 || this.privateChatMode == 1 && this.isFriendOrSelf(text2))) {
               scalar++;
               scalar2++;
            }

            if ((chatType == 3 || chatType == 7)
               && (this.splitpublicChat == 0 || chatViewMode == 2)
               && (chatType == 7 || this.privateChatMode == 0 || this.privateChatMode == 1 && this.isFriendOrSelf(text2))) {
               if (scalarArgument > scalar3 - 14 && scalarArgument <= scalar3) {
                  this.menuActionNames[this.menuActionCount] = "Report abuse @whi@" + text;
                  this.menuActionIds[this.menuActionCount] = 606;
                  this.menuActionCount++;
                  this.menuActionNames[this.menuActionCount] = "Add ignore @whi@" + text;
                  this.menuActionIds[this.menuActionCount] = 42;
                  this.menuActionCount++;
                  this.menuActionNames[this.menuActionCount] = "Add friend @whi@" + text;
                  this.menuActionIds[this.menuActionCount] = 337;
                  this.menuActionCount++;
                  this.menuActionNames[this.menuActionCount] = "Group Inv/Kick @whi@" + text;
                  this.menuActionIds[this.menuActionCount] = 1999;
                  this.menuActionCount++;
               }

               scalar++;
               scalar2++;
            }
         }
      }
   }
   private void buildTradeDuelChatMenu(int scalarArgument) {
      int scalar = 0;
      int scalar2 = -3;

      for (int chatMessageIndex = 0; chatMessageIndex < 100; chatMessageIndex++) {
         if (this.chatMessages[chatMessageIndex] != null && (chatViewMode == 3 || chatViewMode == 4)) {
            int chatType = this.chatTypes[chatMessageIndex];
            String text = this.chatNames[chatMessageIndex];
            int scalar3 = 70 - scalar * 14 + this.chatScrollOffset + 4;
            if (gameframeVersion == 474 && screenMode != 0) {
               scalar3 = 70 - scalar2 * 14 + this.chatScrollOffset + 4 - 2;
            }

            if (scalar3 < -20) {
               break;
            }

            if (chatViewMode == 3 && chatType == 4 && (this.tradeMode == 0 || this.tradeMode == 1 && this.isFriendOrSelf(text))) {
               if (scalarArgument > scalar3 - 14 && scalarArgument <= scalar3) {
                  this.menuActionNames[this.menuActionCount] = "Accept trade @whi@" + text;
                  this.menuActionIds[this.menuActionCount] = 484;
                  this.menuActionCount++;
               }

               scalar++;
               scalar2++;
            }

            if (chatViewMode == 4 && chatType == 8 && (this.tradeMode == 0 || this.tradeMode == 1 && this.isFriendOrSelf(text))) {
               if (scalarArgument > scalar3 - 14 && scalarArgument <= scalar3) {
                  this.menuActionNames[this.menuActionCount] = "Accept challenge @whi@" + text;
                  this.menuActionIds[this.menuActionCount] = 6;
                  this.menuActionCount++;
               }

               scalar++;
               scalar2++;
            }

            if (chatViewMode == 4 && chatType == 255 && (this.tradeMode == 0 || this.tradeMode == 1 && this.isFriendOrSelf(text))) {
               if (scalarArgument > scalar3 - 14 && scalarArgument <= scalar3) {
                  this.menuActionNames[this.menuActionCount] = "Accept group invite @whi@" + text;
                  this.menuActionIds[this.menuActionCount] = 1999;
                  this.menuActionCount++;
               }

               scalar++;
               scalar2++;
            }

            if (chatType == 12) {
               if (scalarArgument > scalar3 - 14 && scalarArgument <= scalar3) {
                  this.menuActionNames[this.menuActionCount] = "Go-to @blu@" + text;
                  this.menuActionIds[this.menuActionCount] = 915;
                  this.menuActionCount++;
               }

               scalar++;
               scalar2++;
            }
         }
      }
   }
   private void buildChatAreaMenu(int scalarArgument) {
      int scalar = 0;
      int scalar2 = -3;
      int chatMessageIndex = 0;

      while (true) {
         processChatMessage: {
            if (chatMessageIndex < 100) {
               if (this.chatMessages[chatMessageIndex] == null) {
                  break processChatMessage;
               }

               int chatType = this.chatTypes[chatMessageIndex];
               int scalar3 = 70 - scalar * 14 + this.chatScrollOffset + 4;
               if (gameframeVersion == 474 && screenMode != 0) {
                  scalar3 = 70 - scalar2 * 14 + this.chatScrollOffset + 4 - 2;
               }

               if (scalar3 >= -20) {
                  String text;
                  String text2 = text = this.chatNames[chatMessageIndex];
                  text = buildChatNameIcons(this.chatPrivileges[chatMessageIndex], this.chatDonatorStatuses[chatMessageIndex], this.chatAccountModes[chatMessageIndex]) + text;
                  if (chatViewMode == 1) {
                     this.buildPublicChatMenu(scalarArgument);
                     return;
                  }

                  if (chatViewMode == 2) {
                     this.buildPrivateChatMenu(scalarArgument);
                     return;
                  }

                  if (chatViewMode == 3 || chatViewMode == 4) {
                     this.buildTradeDuelChatMenu(scalarArgument);
                     return;
                  }

                  if (chatViewMode != 5) {
                     if (chatType == 0) {
                        scalar++;
                        scalar2++;
                     }

                     if ((chatType == 1 || chatType == 2 || chatType == 98) && (chatType == 1 || this.publicChatMode == 0 || this.publicChatMode == 1 && this.isFriendOrSelf(text2))) {
                        if (scalarArgument > scalar3 - 14 && scalarArgument <= scalar3 && !text2.equals(localPlayer.name)) {
                           this.menuActionNames[this.menuActionCount] = "Report abuse @whi@" + text;
                           this.menuActionIds[this.menuActionCount] = 606;
                           this.menuActionCount++;
                           this.menuActionNames[this.menuActionCount] = "Add ignore @whi@" + text;
                           this.menuActionIds[this.menuActionCount] = 42;
                           this.menuActionCount++;
                           this.menuActionNames[this.menuActionCount] = "Add friend @whi@" + text;
                           this.menuActionIds[this.menuActionCount] = 337;
                           this.menuActionCount++;
                           this.menuActionNames[this.menuActionCount] = "Group Inv/Kick @whi@" + text;
                           this.menuActionIds[this.menuActionCount] = 1999;
                           this.menuActionCount++;
                        }

                        scalar++;
                        scalar2++;
                     }

                     if ((chatType == 3 || chatType == 7) && this.splitpublicChat == 0 && (chatType == 7 || this.privateChatMode == 0 || this.publicChatMode == 1 && this.isFriendOrSelf(text2))
                        )
                      {
                        if (scalarArgument > scalar3 - 14 && scalarArgument <= scalar3) {
                           this.menuActionNames[this.menuActionCount] = "Report abuse @whi@" + text;
                           this.menuActionIds[this.menuActionCount] = 606;
                           this.menuActionCount++;
                           this.menuActionNames[this.menuActionCount] = "Add ignore @whi@" + text;
                           this.menuActionIds[this.menuActionCount] = 42;
                           this.menuActionCount++;
                           this.menuActionNames[this.menuActionCount] = "Add friend @whi@" + text;
                           this.menuActionIds[this.menuActionCount] = 337;
                           this.menuActionCount++;
                           this.menuActionNames[this.menuActionCount] = "Group Inv/Kick @whi@" + text;
                           this.menuActionIds[this.menuActionCount] = 1999;
                           this.menuActionCount++;
                        }

                        scalar++;
                        scalar2++;
                     }

                     if (chatType == 4 && (this.tradeMode == 0 || this.tradeMode == 1 && this.isFriendOrSelf(text2))) {
                        if (scalarArgument > scalar3 - 14 && scalarArgument <= scalar3) {
                           this.menuActionNames[this.menuActionCount] = "Accept trade @whi@" + text;
                           this.menuActionIds[this.menuActionCount] = 484;
                           this.menuActionCount++;
                        }

                        scalar++;
                        scalar2++;
                     }

                     if ((chatType == 5 || chatType == 6) && this.splitpublicChat == 0 && this.privateChatMode < 2) {
                        scalar++;
                        scalar2++;
                     }

                     if (chatType == 8 && (this.tradeMode == 0 || this.tradeMode == 1 && this.isFriendOrSelf(text2))) {
                        if (scalarArgument > scalar3 - 14 && scalarArgument <= scalar3) {
                           this.menuActionNames[this.menuActionCount] = "Accept challenge @whi@" + text;
                           this.menuActionIds[this.menuActionCount] = 6;
                           this.menuActionCount++;
                        }

                        scalar++;
                        scalar2++;
                     }

                     if (chatType == 255 && (this.tradeMode == 0 || this.tradeMode == 1 && this.isFriendOrSelf(text2))) {
                        if (scalarArgument > scalar3 - 14 && scalarArgument <= scalar3) {
                           this.menuActionNames[this.menuActionCount] = "Accept group invite @whi@" + text;
                           this.menuActionIds[this.menuActionCount] = 1999;
                           this.menuActionCount++;
                        }

                        scalar++;
                        scalar2++;
                     }
                     break processChatMessage;
                  }
               }
            }

            return;
         }

         chatMessageIndex++;
      }
   }
   private void pushMessage(String text, int chatType, String newText, int chatPrivilege, int chatDonatorStatus, int chatAccountMode) {
      if (chatType == 0 && this.dialogID != -1) {
         this.clickToContinueString = text;
         super.clickButton = 0;
      }

      if (this.backDialogID == -1) {
         this.inputTaken = true;
      }

      for (int chatTypeIndex = 99; chatTypeIndex > 0; chatTypeIndex--) {
         this.chatTypes[chatTypeIndex] = this.chatTypes[chatTypeIndex - 1];
         this.chatNames[chatTypeIndex] = this.chatNames[chatTypeIndex - 1];
         this.chatMessages[chatTypeIndex] = this.chatMessages[chatTypeIndex - 1];
         this.chatPrivileges[chatTypeIndex] = this.chatPrivileges[chatTypeIndex - 1];
         this.chatDonatorStatuses[chatTypeIndex] = this.chatDonatorStatuses[chatTypeIndex - 1];
         this.chatAccountModes[chatTypeIndex] = this.chatAccountModes[chatTypeIndex - 1];
      }

      this.chatTypes[0] = chatType;
      this.chatNames[0] = newText;
      this.chatPrivileges[0] = chatPrivilege;
      this.chatDonatorStatuses[0] = chatDonatorStatus;
      this.chatAccountModes[0] = chatAccountMode;
      this.chatMessages[0] = text;
   }
   private void processTabClick() {
      int canvasWidth = customSprites[this.tabBarBackgroundSpriteId].canvasWidth;
      int customSprites2 = customSprites[this.tabBarBackgroundSpriteId].canvasHeight;
      if (super.clickButton == 1) {
         if (clientWidth >= this.wideTabBarWidthThreshold) {
            int[] values = new int[]{
               0, canvasWidth, canvasWidth << 1, canvasWidth * 3, canvasWidth << 2, canvasWidth * 5, canvasWidth * 6, canvasWidth * 7, canvasWidth << 3, canvasWidth * 9, canvasWidth * 10, canvasWidth * 11, canvasWidth * 12, canvasWidth * 13
            };

            for (int sourceCurrentTab = 0; sourceCurrentTab < 14; sourceCurrentTab++) {
               if (sourceCurrentTab != 7
                  && super.clickX >= clientWidth - canvasWidth * 14 + values[sourceCurrentTab]
                  && super.clickX <= clientWidth - canvasWidth * 14 + values[sourceCurrentTab] + canvasWidth
                  && super.clickY >= clientHeight - customSprites2
                  && super.clickY <= clientHeight
                  && this.tabInterfaceIds[sourceCurrentTab] != -1) {
                  Client client;
                  boolean sourceResizableTabPanelVisible;
                  updateWideTabPanelVisibility: {
                     if (this.currentTab != sourceCurrentTab) {
                        this.needDrawTabArea = true;
                        this.currentTab = sourceCurrentTab;
                        this.tabAreaAltered = true;
                        client = this;
                     } else {
                        client = this;
                        if (this.resizableTabPanelVisible) {
                           sourceResizableTabPanelVisible = false;
                           break updateWideTabPanelVisibility;
                        }
                     }

                     sourceResizableTabPanelVisible = true;
                  }

                  client.resizableTabPanelVisible = sourceResizableTabPanelVisible;
               }
            }
         } else {
            int[] integerBuffer = new int[]{0, canvasWidth, canvasWidth << 1, canvasWidth * 3, canvasWidth << 2, canvasWidth * 5, canvasWidth * 6, canvasWidth, canvasWidth, canvasWidth << 1, canvasWidth * 3, canvasWidth << 2, canvasWidth * 5, canvasWidth * 6};
            int[] values2 = new int[]{customSprites2 << 1, customSprites2 << 1, customSprites2 << 1, customSprites2 << 1, customSprites2 << 1, customSprites2 << 1, customSprites2 << 1, customSprites2, customSprites2, customSprites2, customSprites2, customSprites2, customSprites2, customSprites2};

            for (int tabInterfaceIdIndex = 0; tabInterfaceIdIndex < 14; tabInterfaceIdIndex++) {
               if (tabInterfaceIdIndex != 7
                  && super.clickX >= clientWidth - canvasWidth * 7 + integerBuffer[tabInterfaceIdIndex]
                  && super.clickX <= clientWidth - canvasWidth * 7 + integerBuffer[tabInterfaceIdIndex] + canvasWidth
                  && super.clickY >= clientHeight - values2[tabInterfaceIdIndex]
                  && super.clickY <= clientHeight - values2[tabInterfaceIdIndex] + customSprites2
                  && this.tabInterfaceIds[tabInterfaceIdIndex] != -1) {
                  Client client2;
                  boolean resizableTabPanelVisible2;
                  updateCompactTabPanelVisibility: {
                     if (this.currentTab != tabInterfaceIdIndex) {
                        this.needDrawTabArea = true;
                        this.currentTab = tabInterfaceIdIndex;
                        this.tabAreaAltered = true;
                        client2 = this;
                     } else {
                        client2 = this;
                        if (this.resizableTabPanelVisible) {
                           resizableTabPanelVisible2 = false;
                           break updateCompactTabPanelVisibility;
                        }
                     }

                     resizableTabPanelVisible2 = true;
                  }

                  client2.resizableTabPanelVisible = resizableTabPanelVisible2;
               }
            }
         }
      }

      if (this.flashingSidebarId == this.currentTab) {
         this.outgoingBuffer.writeOpcode(152);
         this.outgoingBuffer.writeByte(this.currentTab);
      }
   }
   private void setupGameScreenBuffers() {
      if (this.chatboxImageProducer == null) {
         this.unloadTitleScreen();
         super.graphicsBuffer = null;
         this.topLeft1BackgroundTile = null;
         this.bottomLeft1BackgroundTile = null;
         this.flameLeftBackground = null;
         this.flameRightBackground = null;
         this.bottomLeft0BackgroundTile = null;
         this.bottomRightImageProducer = null;
         this.loginMusicImageProducer = null;
         this.middleLeft1BackgroundTile = null;
         this.middleRightBackgroundBuffer = null;
         int sourceModernChatRasterWidth = modernChatRasterWidth;
         int sourceModernChatRasterHeight = modernChatRasterHeight;
         this.getGameComponent();
         new BufferedImageGraphicsBuffer(sourceModernChatRasterWidth, sourceModernChatRasterHeight);
         int sourceClassicChatRasterWidth = classicChatRasterWidth;
         int sourceClassicChatRasterHeight = classicChatRasterHeight;
         this.getGameComponent();
         this.chatboxImageProducer = new BufferedImageGraphicsBuffer(sourceClassicChatRasterWidth, sourceClassicChatRasterHeight);
         if (gameframeVersion == 474) {
            sourceClassicChatRasterWidth = modernChatRasterWidth;
            sourceClassicChatRasterHeight = modernChatRasterHeight;
            this.getGameComponent();
            this.chatboxImageProducer = new BufferedImageGraphicsBuffer(sourceClassicChatRasterWidth, sourceClassicChatRasterHeight);
         }

         if (gameframeVersion == 474 && orbsEnabled) {
            this.getGameComponent();
            this.minimapImageProducer = new BufferedImageGraphicsBuffer(201, 156);
         } else {
            this.getGameComponent();
            this.minimapImageProducer = new BufferedImageGraphicsBuffer(172, 156);
         }

         Rasterizer2D.clear();
         if (screenMode == 0) {
            if (gameframeVersion == 474 && orbsEnabled) {
               customSprites[25].drawSprite(0, 0);
               mapBack.drawBackground(29, 0);
            } else {
               mapBack.drawBackground(0, 0);
            }
         }

         sourceClassicChatRasterWidth = musicVolume;
         this.getGameComponent();
         this.tabImageProducer = new BufferedImageGraphicsBuffer(sourceClassicChatRasterWidth, 261);
         if (gameframeVersion == 474) {
            sourceClassicChatRasterWidth = modernSidebarRasterWidth;
            this.getGameComponent();
            this.tabImageProducer = new BufferedImageGraphicsBuffer(sourceClassicChatRasterWidth, 261);
         }

         sourceClassicChatRasterWidth = screenMode == 0 ? 512 : clientWidth;
         sourceClassicChatRasterHeight = screenMode == 0 ? 334 : clientHeight;
         this.getGameComponent();
         this.gameScreenImageProducer = new BufferedImageGraphicsBuffer(sourceClassicChatRasterWidth, sourceClassicChatRasterHeight);
         Rasterizer2D.clear();
         this.getGameComponent();
         this.bottomFrameStripBuffer = new BufferedImageGraphicsBuffer(496, 50);
         this.getGameComponent();
         this.bottomRightFrameStripBuffer = new BufferedImageGraphicsBuffer(269, 37);
         this.getGameComponent();
         this.topRightFrameStripBuffer = new BufferedImageGraphicsBuffer(249, 45);
         this.getGameComponent();
         this.rightFrameStripBuffer = new BufferedImageGraphicsBuffer(22, 261);
         this.getGameComponent();
         this.middleRightFrameStripBuffer = new BufferedImageGraphicsBuffer(37, 133);
         this.getGameComponent();
         this.chatRightFrameStripBuffer = new BufferedImageGraphicsBuffer(57, 109);
         this.getGameComponent();
         this.chatTopFrameStripBuffer = new BufferedImageGraphicsBuffer(553, 19);
         this.getGameComponent();
         this.chatLeftFrameStripBuffer = new BufferedImageGraphicsBuffer(17, 96);
         this.getGameComponent();
         this.minimapRightFrameStripBuffer = new BufferedImageGraphicsBuffer(48, 156);
         this.getGameComponent();
         this.minimapLeftFrameStripBuffer = new BufferedImageGraphicsBuffer(34, 156);
         this.getGameComponent();
         this.chatSettingImageProducer = new BufferedImageGraphicsBuffer(765, 4);
         if (gameframeVersion == 474) {
            this.getGameComponent();
            this.rightFrameStripBuffer = new BufferedImageGraphicsBuffer(28, 261);
            this.getGameComponent();
            this.bottomFrameStripBuffer = new BufferedImageGraphicsBuffer(520, 27);
            this.getGameComponent();
            this.chatLeftFrameStripBuffer = new BufferedImageGraphicsBuffer(7, 131);
            this.getGameComponent();
            this.chatRightFrameStripBuffer = new BufferedImageGraphicsBuffer(34, 121);
            this.getGameComponent();
            this.chatTopFrameStripBuffer = new BufferedImageGraphicsBuffer(547, 7);
            this.getGameComponent();
            this.minimapLeftFrameStripBuffer = new BufferedImageGraphicsBuffer(29, 156);
            this.getGameComponent();
            this.middleRightFrameStripBuffer = new BufferedImageGraphicsBuffer(31, 133);
         }

         this.welcomeScreenRaised = true;
      }
   }
   private void drawMinimapHint(Sprite sprite, int scalarArgument, int scalarArgument2) {
      int sINEEntry;
      if ((sINEEntry = scalarArgument2 * scalarArgument2 + scalarArgument * scalarArgument) > 4225 && sINEEntry < 90000) {
         int sINEIndex = this.minimapInt1 + this.minimapInt2 & 2047;
         sINEEntry = Model.SINE[sINEIndex];
         int cOSINEEntry = Model.COSINE[sINEIndex];
         sINEEntry = (sINEEntry << 8) / (this.minimapInt3 + 256);
         int scalar = (cOSINEEntry << 8) / (this.minimapInt3 + 256);
         int scalar2 = scalarArgument * sINEEntry + scalarArgument2 * scalar >> 16;
         int scalar3 = scalarArgument * scalar - scalarArgument2 * sINEEntry >> 16;
         double calculation;
         int scalar4 = (int)(Math.sin(calculation = Math.atan2(scalar2, scalar3)) * 63.0);
         scalarArgument = (int)(Math.cos(calculation) * 57.0);
         if (screenMode == 0) {
            this.mapEdge.drawRotated20x20(83 - scalarArgument - 20, calculation, scalar4 + 94 + 4 - 10);
         } else {
            this.mapEdge.drawRotated20x20(83 - scalarArgument - 20 + 9, calculation, scalar4 + 94 + 4 - 10 + clientWidth - 207);
         }
      } else {
         this.drawMinimapIcon(sprite, scalarArgument2, scalarArgument);
      }
   }
   private void rightClickChatButtons() {
      if (super.mouseX >= 5 && super.mouseX <= 61 && super.mouseY >= clientHeight - 23 && super.mouseY <= clientHeight) {
         this.menuActionNames[1] = "View All";
         this.menuActionIds[1] = 999;
         this.menuActionCount = 2;
      } else if (super.mouseX >= 71 && super.mouseX <= 127 && super.mouseY >= clientHeight - 23 && super.mouseY <= clientHeight) {
         this.menuActionNames[1] = "View Game";
         this.menuActionIds[1] = 998;
         this.menuActionCount = 2;
      } else if (super.mouseX >= 137 && super.mouseX <= 193 && super.mouseY >= clientHeight - 23 && super.mouseY <= clientHeight) {
         this.menuActionNames[1] = "Hide Public";
         this.menuActionIds[1] = 997;
         this.menuActionNames[2] = "Off Public";
         this.menuActionIds[2] = 996;
         this.menuActionNames[3] = "Friends Public";
         this.menuActionIds[3] = 995;
         this.menuActionNames[4] = "On Public";
         this.menuActionIds[4] = 994;
         this.menuActionNames[5] = "View Public";
         this.menuActionIds[5] = 993;
         this.menuActionCount = 6;
      } else if (super.mouseX >= 203 && super.mouseX <= 259 && super.mouseY >= clientHeight - 23 && super.mouseY <= clientHeight) {
         this.menuActionNames[1] = "Off Private";
         this.menuActionIds[1] = 992;
         this.menuActionNames[2] = "Friends Private";
         this.menuActionIds[2] = 991;
         this.menuActionNames[3] = "On Private";
         this.menuActionIds[3] = 990;
         this.menuActionNames[4] = "View Private";
         this.menuActionIds[4] = 989;
         this.menuActionCount = 5;
      } else {
         if (super.mouseX >= 335 && super.mouseX <= 391 && super.mouseY >= clientHeight - 23 && super.mouseY <= clientHeight) {
            this.menuActionNames[1] = "Off Trade";
            this.menuActionIds[1] = 987;
            this.menuActionNames[2] = "Friends Trade";
            this.menuActionIds[2] = 986;
            this.menuActionNames[3] = "On Trade";
            this.menuActionIds[3] = 985;
            this.menuActionNames[4] = "View Trade";
            this.menuActionIds[4] = 984;
            this.menuActionCount = 5;
         }
      }
   }
   private void processRightClick() {
      if (this.activeInterfaceType == 0) {
         this.menuActionNames[0] = "Cancel";
         this.menuActionIds[0] = 1107;
         this.menuActionCount = 1;
         priorityMenuActionIndex = -1;
         if (this.fullscreenInterfaceId != -1) {
            this.hoveredWidgetId = 0;
            this.hoveredTooltipWidgetId = 0;
            if (screenMode == 0) {
               this.buildInterfaceMenu(0, Widget.widgets[this.fullscreenInterfaceId], super.mouseX, 0, super.mouseY, 0);
            } else {
               this.buildInterfaceMenu(clientWidth / 2 - 382 + 4, Widget.widgets[this.fullscreenInterfaceId], super.mouseX, clientHeight / 2 - 251 + 4, super.mouseY, 0);
            }

            if (this.hoveredWidgetId != this.viewportHoverWidgetId) {
               this.viewportHoverWidgetId = this.hoveredWidgetId;
            }

            if (this.hoveredTooltipWidgetId != this.viewportTooltipWidgetId) {
               this.viewportTooltipWidgetId = this.hoveredTooltipWidgetId;
            }
         } else {
            Client client = this;
            if (this.splitpublicChat != 0) {
               int scalar = 0;
               if (client.systemUpdateTime != 0) {
                  scalar = 1;
               }

               for (int chatMessageIndex = 0; chatMessageIndex < 100; chatMessageIndex++) {
                  if (client.chatMessages[chatMessageIndex] != null) {
                     int chatType = client.chatTypes[chatMessageIndex];
                     String text;
                     String text3 = text = client.chatNames[chatMessageIndex];
                     text = buildChatNameIcons(client.chatPrivileges[chatMessageIndex], client.chatDonatorStatuses[chatMessageIndex], client.chatAccountModes[chatMessageIndex]) + text;
                     if ((chatType == 3 || chatType == 7) && (chatType == 7 || client.privateChatMode == 0 || client.privateChatMode == 1 && client.isFriendOrSelf(text3))) {
                        int localClientHeight = 329 - scalar * 13;
                        if (screenMode != 0) {
                           localClientHeight = clientHeight - 174 - scalar * 13;
                        }

                        if (client.mouseX > (screenMode == 0 ? 4 : 0)
                           && client.mouseY - (screenMode == 0 ? 4 : 0) > localClientHeight - 10
                           && client.mouseY - 4 <= localClientHeight + 3) {
                           int localPlainFont;
                           if ((localPlainFont = client.plainFont.getTextWidth("From:  " + text + client.chatMessages[chatMessageIndex]) + 25) > 450) {
                              localPlainFont = 450;
                           }

                           if (client.mouseX < (screenMode == 0 ? 4 : 0) + localPlainFont) {
                              client.menuActionNames[client.menuActionCount] = "Group Inv/Kick @whi@" + text;
                              client.menuActionIds[client.menuActionCount] = 1999;
                              client.menuActionCount++;
                              client.menuActionNames[client.menuActionCount] = "Report abuse @whi@" + text;
                              client.menuActionIds[client.menuActionCount] = 2606;
                              client.menuActionCount++;
                              client.menuActionNames[client.menuActionCount] = "Add ignore @whi@" + text;
                              client.menuActionIds[client.menuActionCount] = 2042;
                              client.menuActionCount++;
                              client.menuActionNames[client.menuActionCount] = "Add friend @whi@" + text;
                              client.menuActionIds[client.menuActionCount] = 2337;
                              client.menuActionCount++;
                           }
                        }

                        if (++scalar >= 5) {
                           break;
                        }
                     }

                     if ((chatType == 5 || chatType == 6) && client.privateChatMode < 2) {
                        if (++scalar >= 5) {
                           break;
                        }
                     }
                  }
               }
            }

            this.hoveredWidgetId = 0;
            this.hoveredTooltipWidgetId = 0;
            if (screenMode == 0) {
               if (super.mouseX > 4 && super.mouseY > 4 && super.mouseX < 516 && super.mouseY < 338) {
                  if (this.openInterfaceId != -1) {
                     this.buildInterfaceMenu(4, Widget.widgets[this.openInterfaceId], super.mouseX, 4, super.mouseY, 0);
                  } else {
                     this.build3dScreenMenu();
                  }
               }
            } else {
               client = this;
               boolean flag;
               if ((!this.isMouseInRectangle(0, clientHeight - 165, 519, clientHeight) || !client.chatMessagesVisible) && !client.isMouseInRectangle(0, clientHeight - 22, 519, clientHeight)) {
                  if (client.isMouseInRectangle(clientWidth - 235, 0, clientWidth, 177)) {
                     flag = false;
                  } else if (osrsResizableFrame && client.isMouseInRectangle(clientWidth - 241, clientHeight - 335, clientWidth, clientHeight)) {
                     flag = false;
                  } else {
                     checkGameViewportMenuArea: {
                        if (clientWidth >= client.wideTabBarWidthThreshold) {
                           if (client.isMouseInRectangle(
                                 clientWidth - customSprites[client.tabBarBackgroundSpriteId].canvasWidth * 14,
                                 clientHeight - customSprites[client.tabBarBackgroundSpriteId].canvasHeight,
                                 clientWidth,
                                 clientHeight
                              )
                              || client.isMouseInRectangle(clientWidth - 204, clientHeight - customSprites[client.tabBarBackgroundSpriteId].canvasHeight - 274, clientWidth, clientHeight)
                                 && client.resizableTabPanelVisible) {
                              flag = false;
                              break checkGameViewportMenuArea;
                           }
                        } else if (client.isMouseInRectangle(
                              clientWidth - customSprites[client.tabBarBackgroundSpriteId].canvasWidth * 7,
                              clientHeight - (customSprites[client.tabBarBackgroundSpriteId].canvasHeight << 1),
                              clientWidth,
                              clientHeight
                           )
                           || client.isMouseInRectangle(clientWidth - 204, clientHeight - (customSprites[client.tabBarBackgroundSpriteId].canvasHeight << 1) - 274, clientWidth, clientHeight)
                              && client.resizableTabPanelVisible) {
                           flag = false;
                           break checkGameViewportMenuArea;
                        }

                        flag = true;
                     }
                  }
               } else {
                  flag = false;
               }

               if (flag) {
                  Widget widget = null;
                  if (this.openInterfaceId != -1) {
                     widget = Widget.widgets[this.openInterfaceId];
                  }

                  if (shouldCenterInterface(widget)) {
                     if (this.openInterfaceId != -1
                        && super.mouseX > clientWidth / 2 - 256 + 4
                        && super.mouseY > clientHeight / 2 - 167 + 4
                        && super.mouseX < clientWidth / 2 + 256 + 4
                        && super.mouseY < clientHeight / 2 + 167 + 4) {
                        this.buildInterfaceMenu(
                           clientWidth / 2 - 256 + 4, Widget.widgets[this.openInterfaceId], super.mouseX, clientHeight / 2 - 167 + 4, super.mouseY, 0
                        );
                     } else if (!this.fullscreenInterfaceBackdropVisible && !this.fullscreenInterfaceCoversViewport) {
                        this.build3dScreenMenu();
                     }
                  } else if (this.openInterfaceId != -1 && super.mouseX > 4 && super.mouseY > 4 && super.mouseX < 516 && super.mouseY < 338) {
                     this.buildInterfaceMenu(4, Widget.widgets[this.openInterfaceId], super.mouseX, 4, super.mouseY, 0);
                  } else if (!this.fullscreenInterfaceBackdropVisible && !this.fullscreenInterfaceCoversViewport) {
                     this.build3dScreenMenu();
                  }
               }
            }

            if (this.hoveredWidgetId != this.viewportHoverWidgetId) {
               this.viewportHoverWidgetId = this.hoveredWidgetId;
            }

            if (this.hoveredTooltipWidgetId != this.viewportTooltipWidgetId) {
               this.viewportTooltipWidgetId = this.hoveredTooltipWidgetId;
            }

            this.hoveredWidgetId = 0;
            this.hoveredTooltipWidgetId = 0;
            if (gameframeVersion != 474) {
               if (super.mouseX > 553 && super.mouseY > 205 && super.mouseX < 743 && super.mouseY < 466) {
                  if (this.invOverlayInterfaceID != -1) {
                     this.buildInterfaceMenu(553, Widget.widgets[this.invOverlayInterfaceID], super.mouseX, 205, super.mouseY, 0);
                  } else if (this.tabInterfaceIds[this.currentTab] != -1) {
                     this.buildInterfaceMenu(553, Widget.widgets[this.tabInterfaceIds[this.currentTab]], super.mouseX, 205, super.mouseY, 0);
                  }
               }
            } else if (screenMode == 0) {
               if (super.mouseX > 553 && super.mouseY > 205 && super.mouseX < 743 && super.mouseY < 466) {
                  if (this.invOverlayInterfaceID != -1) {
                     this.buildInterfaceMenu(547, Widget.widgets[this.invOverlayInterfaceID], super.mouseX, 205, super.mouseY, 0);
                  } else if (this.tabInterfaceIds[this.currentTab] != -1) {
                     this.buildInterfaceMenu(547, Widget.widgets[this.tabInterfaceIds[this.currentTab]], super.mouseX, 205, super.mouseY, 0);
                  }
               }
            } else if (osrsResizableFrame) {
               int localClientWidth = clientWidth - customSprites[97].canvasWidth;
               int clientHeight2 = clientHeight - customSprites[97].canvasHeight;
               if (super.mouseX > localClientWidth + 26 && super.mouseY > clientHeight2 + 37 && super.mouseX < localClientWidth + 216 && super.mouseY < clientHeight2 + 497) {
                  if (this.invOverlayInterfaceID != -1) {
                     this.buildInterfaceMenu(localClientWidth + 30, Widget.widgets[this.invOverlayInterfaceID], super.mouseX, clientHeight2 + 41, super.mouseY, 0);
                  } else if (this.tabInterfaceIds[this.currentTab] != -1) {
                     this.buildInterfaceMenu(localClientWidth + 30, Widget.widgets[this.tabInterfaceIds[this.currentTab]], super.mouseX, clientHeight2 + 41, super.mouseY, 0);
                  }
               }
            } else {
               int clientWidth2 = clientWidth >= this.wideTabBarWidthThreshold ? 37 : 74;
               if (super.mouseX > clientWidth - 197
                  && super.mouseY > clientHeight - clientWidth2 - 267
                  && super.mouseX < clientWidth - 7
                  && super.mouseY < clientHeight - clientWidth2 - 7
                  && this.resizableTabPanelVisible) {
                  if (this.invOverlayInterfaceID != -1) {
                     this.buildInterfaceMenu(clientWidth - 193, Widget.widgets[this.invOverlayInterfaceID], super.mouseX, clientHeight - clientWidth2 - 263, super.mouseY, 0);
                  } else if (this.tabInterfaceIds[this.currentTab] != -1) {
                     this.buildInterfaceMenu(
                        clientWidth - 193, Widget.widgets[this.tabInterfaceIds[this.currentTab]], super.mouseX, clientHeight - clientWidth2 - 263, super.mouseY, 0
                     );
                  }
               }
            }

            if (this.hoveredWidgetId != this.tabHoverWidgetId) {
               this.needDrawTabArea = true;
               this.tabAreaAltered = true;
               this.tabHoverWidgetId = this.hoveredWidgetId;
            }

            if (this.hoveredTooltipWidgetId != this.tabTooltipWidgetId) {
               this.needDrawTabArea = true;
               this.tabAreaAltered = true;
               this.tabTooltipWidgetId = this.hoveredTooltipWidgetId;
            }

            buildChatAreaContextMenu: {
               this.hoveredWidgetId = 0;
               this.hoveredTooltipWidgetId = 0;
               Client client2;
               int localMouseY;
               short shortCode;
               if (gameframeVersion != 474) {
                  if (super.mouseX <= 17 || super.mouseY <= 357 || super.mouseX >= 496 || super.mouseY >= 453) {
                     break buildChatAreaContextMenu;
                  }

                  if (this.backDialogID != -1) {
                     this.buildInterfaceMenu(17, Widget.widgets[this.backDialogID], super.mouseX, 357, super.mouseY, 0);
                     break buildChatAreaContextMenu;
                  }

                  if (super.mouseY >= 434 || super.mouseX >= 426 || this.inputDialogState == 3) {
                     break buildChatAreaContextMenu;
                  }

                  client2 = this;
                  localMouseY = super.mouseY;
                  shortCode = 357;
               } else if (screenMode == 0) {
                  if (super.mouseX <= 7 || super.mouseY <= 345 || super.mouseX >= 512 || super.mouseY >= 473) {
                     break buildChatAreaContextMenu;
                  }

                  if (this.backDialogID != -1) {
                     this.buildInterfaceMenu(7, Widget.widgets[this.backDialogID], super.mouseX, 360, super.mouseY, 0);
                     break buildChatAreaContextMenu;
                  }

                  if (super.mouseY >= 459 || super.mouseX >= 446 || this.inputDialogState == 3) {
                     break buildChatAreaContextMenu;
                  }

                  client2 = this;
                  localMouseY = super.mouseY;
                  shortCode = 380;
               } else {
                  if (super.mouseX <= 0
                     || super.mouseY <= (screenMode == 0 ? 338 : clientHeight - 158)
                     || super.mouseX >= 490
                     || super.mouseY >= (screenMode == 0 ? 463 : clientHeight - 33)
                     || !this.chatMessagesVisible) {
                     break buildChatAreaContextMenu;
                  }

                  if (this.backDialogID != -1) {
                     this.buildInterfaceMenu(20, Widget.widgets[this.backDialogID], super.mouseX, screenMode == 0 ? 358 : clientHeight - 138, super.mouseY, 0);
                     break buildChatAreaContextMenu;
                  }

                  if (super.mouseY >= (screenMode == 0 ? 463 : clientHeight - 33) || super.mouseX >= 490 || this.inputDialogState == 3) {
                     break buildChatAreaContextMenu;
                  }

                  client2 = this;
                  localMouseY = super.mouseY;
                  shortCode = (short)(screenMode == 0 ? 338 : clientHeight - 158);
               }

               client2.buildChatAreaMenu(localMouseY - shortCode);
            }

            if (this.backDialogID != -1 && this.hoveredWidgetId != this.chatHoverWidgetId) {
               this.inputTaken = true;
               this.chatHoverWidgetId = this.hoveredWidgetId;
            }

            if (this.backDialogID != -1 && this.hoveredTooltipWidgetId != this.chatTooltipWidgetId) {
               this.inputTaken = true;
               this.chatTooltipWidgetId = this.hoveredTooltipWidgetId;
            }

            if (screenMode == 0 && gameframeVersion == 474) {
               if (super.mouseX > 4 && super.mouseY > 480 && super.mouseX < 516 && super.mouseY < 503) {
                  this.rightClickChatButtons();
               }

               if (orbsEnabled && this.fullscreenInterfaceId == -1) {
                  this.processRunOrbHover();
               }
            }

            if (screenMode != 0) {
               if (super.mouseX > 0 && super.mouseY > clientHeight - 165 && super.mouseX < 519 && super.mouseY < clientHeight) {
                  this.rightClickChatButtons();
               }

               if (orbsEnabled && this.fullscreenInterfaceId == -1) {
                  this.processRunOrbHover();
               }
            }

            if (!this.menuOpen && this.inputDialogState == 3) {
               this.buildItemSearchMenu(super.mouseY);
            }

            boolean localFlag = false;

            while (!localFlag) {
               localFlag = true;

               for (int menuActionIdIndex = 0; menuActionIdIndex < this.menuActionCount - 1; menuActionIdIndex++) {
                  if (this.menuActionIds[menuActionIdIndex] < 1000 && this.menuActionIds[menuActionIdIndex + 1] > 1000) {
                     String text2 = this.menuActionNames[menuActionIdIndex];
                     this.menuActionNames[menuActionIdIndex] = this.menuActionNames[menuActionIdIndex + 1];
                     this.menuActionNames[menuActionIdIndex + 1] = text2;
                     int menuActionIdOrMenuActionIds = this.menuActionIds[menuActionIdIndex];
                     this.menuActionIds[menuActionIdIndex] = this.menuActionIds[menuActionIdIndex + 1];
                     this.menuActionIds[menuActionIdIndex + 1] = menuActionIdOrMenuActionIds;
                     int menuParam0Entry = this.menuParam0[menuActionIdIndex];
                     this.menuParam0[menuActionIdIndex] = this.menuParam0[menuActionIdIndex + 1];
                     this.menuParam0[menuActionIdIndex + 1] = menuParam0Entry;
                     int menuParam1Entry = this.menuParam1[menuActionIdIndex];
                     this.menuParam1[menuActionIdIndex] = this.menuParam1[menuActionIdIndex + 1];
                     this.menuParam1[menuActionIdIndex + 1] = menuParam1Entry;
                     int menuParam2Entry = this.menuParam2[menuActionIdIndex];
                     this.menuParam2[menuActionIdIndex] = this.menuParam2[menuActionIdIndex + 1];
                     this.menuParam2[menuActionIdIndex + 1] = menuParam2Entry;
                     localFlag = false;
                  }
               }
            }
         }
      }
   }
   private void requestMusicTrackImmediate(int newRequestedMusicVolume, int newNextSong) {
      if (isMidiPlayerAvailable() && newNextSong != this.nextSong) {
         this.nextSong = newNextSong;
         this.onDemandFetcher.provide(2, this.nextSong);
         requestedMusicVolume = newRequestedMusicVolume;
         musicTransitionDelay = -1;
         requestedMusicLoop = true;
         musicFadeDuration = -1;
      }
   }
   private static int blendColors(int warmFlamePaletteEntry, int warmFlamePaletteEntry2, int scalarArgument) {
      int scalar = 256 - scalarArgument;
      return ((warmFlamePaletteEntry & 16711935) * scalar + (warmFlamePaletteEntry2 & 16711935) * scalarArgument & -16711936) + ((warmFlamePaletteEntry & 0xFF00) * scalar + (warmFlamePaletteEntry2 & 0xFF00) * scalarArgument & 0xFF0000) >> 8;
   }
   private void login(String text, String newText, boolean flag) {
      SignLink.unusedPublicString2 = text;

      try {
         if (!flag) {
            this.loginMessage1 = "";
            this.loginMessage2 = "Connecting to server...";
            this.drawLoginScreen(true);
         }

         this.connection = new BufferedConnection(this, this.openSocket(transparentTabArea ? 5555 : 43594));
         int sourceYCameraCurve = (int)(NameUtils.encodeBase37(text) >> 16 & 31L);
         this.outgoingBuffer.currentPosition = 0;
         this.outgoingBuffer.writeByte(14);
         this.outgoingBuffer.writeByte(sourceYCameraCurve);
         this.connection.queueBytes(2, this.outgoingBuffer.buffer);

         for (int loopIndex = 0; loopIndex < 8; loopIndex++) {
            this.connection.read();
         }

         int scalar = sourceYCameraCurve = this.connection.read();
         if (sourceYCameraCurve == 0) {
            this.connection.flushInputStream(this.inStream.buffer, 8);
            this.inStream.currentPosition = 0;
            this.serverSeed = this.inStream.readLong();
            int[] values = new int[]{(int)(Math.random() * 9.9999999E7), (int)(Math.random() * 9.9999999E7), (int)(this.serverSeed >> 32), (int)this.serverSeed};
            this.outgoingBuffer.currentPosition = 0;
            this.outgoingBuffer.writeByte(10);
            this.outgoingBuffer.writeInt(values[0]);
            this.outgoingBuffer.writeInt(values[1]);
            this.outgoingBuffer.writeInt(values[2]);
            this.outgoingBuffer.writeInt(values[3]);
            this.outgoingBuffer.writeInt(SignLink.uid);
            this.outgoingBuffer.writeString(text);
            this.outgoingBuffer.writeString(newText);
            this.outgoingBuffer.encryptRsa();
            this.loginBuffer.currentPosition = 0;
            if (flag) {
               this.loginBuffer.writeByte(18);
            } else {
               this.loginBuffer.writeByte(16);
            }

            this.loginBuffer.writeByte(this.outgoingBuffer.currentPosition + 36 + 1 + 1 + 2 + 6);
            this.loginBuffer.writeByte(255);
            this.loginBuffer.writeShort(21);
            this.loginBuffer.writeByte(0);

            for (int loopIndex2 = 0; loopIndex2 < 6; loopIndex2++) {
               String localText = null;
               HashMap hashMap = new HashMap();
               Enumeration enumeration = NetworkInterface.getNetworkInterfaces();

               while (enumeration.hasMoreElements()) {
                  NetworkInterface networkInterface;
                  byte[] hardwareAddress;
                  if ((hardwareAddress = (networkInterface = (NetworkInterface)enumeration.nextElement()).getHardwareAddress()) != null) {
                     hashMap.put(networkInterface.getName(), hardwareAddress);
                     if (localText == null) {
                        localText = networkInterface.getName();
                     }
                  }
               }

               byte[] byteBuffer = localText != null ? (byte[])hashMap.get(localText) : null;
               byte byteCode = byteBuffer[loopIndex2];
               Buffer loginBuffer = this.loginBuffer;
               this.loginBuffer.buffer[loginBuffer.currentPosition++] = (byte)byteCode;
            }

            for (int archiveCrcIndex = 0; archiveCrcIndex < 9; archiveCrcIndex++) {
               this.loginBuffer.writeInt(this.archiveCrcs[archiveCrcIndex]);
            }

            this.loginBuffer.writeBytes(this.outgoingBuffer.buffer, this.outgoingBuffer.currentPosition, 0);
            this.outgoingBuffer.isaacCipher = new IsaacCipher(values);

            for (int loopIndex3 = 0; loopIndex3 < 4; loopIndex3++) {
               values[loopIndex3] += 50;
            }

            this.incomingIsaacCipher = new IsaacCipher(values);
            this.connection.queueBytes(this.loginBuffer.currentPosition, this.loginBuffer.buffer);
            sourceYCameraCurve = this.connection.read();
         }

         if (sourceYCameraCurve == 1) {
            try {
               Thread.sleep(2000L);
            } catch (Exception exception) {
            }

            this.login(text, newText, flag);
         } else if (sourceYCameraCurve == 2) {
            loggedIn = true;
            this.updateClientWindowSize(false);
            this.currentStats[3] = 1;
            sourceYCameraCurve = this.connection.read();
            this.myPrivilege = this.connection.read();
            this.yCameraCurve = sourceYCameraCurve;
            Widget.updateSkillLevelActions(sourceYCameraCurve);
            boolean localMyPrivilege;
            if (!(localMyPrivilege = this.myPrivilege > 0 || this.yCameraCurve > 0)) {
               localMyPrivilege = this.gameframeSelectionAllowed;
            }

            if (ClientWindow.getInstance() != null) {
               ClientWindow.gameframeMenu.setEnabled(localMyPrivilege);
            }

            if (!localMyPrivilege) {
               if (screenMode != 0) {
                  this.setScreenMode(0);
               }

               gameframeVersion = 317;
               applyGameframeVersion();
            }

            this.rebuildViewportBuffers();
            flagged = this.connection.read() == 1;
            this.xCameraCurve = this.connection.read();
            this.lastClickTime = 0L;
            this.duplicateClickCount = 0;
            this.mouseRecorder.sampleCount = 0;
            super.hasFocus = true;
            this.focusReported = true;
            this.outgoingBuffer.currentPosition = 0;
            this.inStream.currentPosition = 0;
            this.pktType = -1;
            this.lastOpcode = -1;
            this.prevPktType = -1;
            this.prevPktType2 = -1;
            this.pktSize = 0;
            this.timeoutCounter = 0;
            this.systemUpdateTime = 0;
            this.logoutTimer = 0;
            this.hintIconDrawType = 0;
            this.menuActionCount = 0;
            priorityMenuActionIndex = -1;
            this.menuOpen = false;
            super.idleTime = 0;

            for (int chatMessageIndex = 0; chatMessageIndex < 100; chatMessageIndex++) {
               this.chatMessages[chatMessageIndex] = null;
            }

            this.itemSelected = 0;
            this.spellSelected = 0;
            this.loadingStage = 0;
            this.currentSound = 0;
            this.cameraX = (int)(Math.random() * 100.0) - 50;
            this.cameraY = (int)(Math.random() * 110.0) - 55;
            this.cameraRotation = (int)(Math.random() * 80.0) - 40;
            this.minimapInt2 = (int)(Math.random() * 120.0) - 60;
            this.minimapInt3 = (int)(Math.random() * 30.0) - 20;
            this.minimapInt1 = (int)(Math.random() * 20.0) - 10 & 2047;
            this.minimapState = 0;
            this.lastRenderedPlane = -1;
            this.destX = 0;
            this.destY = 0;
            this.playerCount = 0;
            this.npcCount = 0;

            for (int playerIndex = 0; playerIndex < 2048; playerIndex++) {
               this.players[playerIndex] = null;
               this.playerAppearanceBuffers[playerIndex] = null;
            }

            for (int npcIndex = 0; npcIndex < 16384; npcIndex++) {
               this.npcs[npcIndex] = null;
            }

            localPlayer = this.players[2047] = new Player();
            this.projectiles.removeAll();
            this.incompleteAnimables.removeAll();

            for (int groundItemIndex = 0; groundItemIndex < 4; groundItemIndex++) {
               for (int loopIndex4 = 0; loopIndex4 < 104; loopIndex4++) {
                  for (int loopIndex5 = 0; loopIndex5 < 104; loopIndex5++) {
                     this.groundItems[groundItemIndex][loopIndex4][loopIndex5] = null;
                  }
               }
            }

            this.spawns = new NodeDeque();
            this.friendServerStatus = 0;
            this.friendCount = 0;
            this.dialogID = -1;
            this.fullscreenInterfaceId = -1;
            this.backDialogID = -1;
            this.openInterfaceId = -1;
            this.fullscreenInterfaceBackdropVisible = false;
            this.fullscreenInterfaceCoversViewport = false;
            this.invOverlayInterfaceID = -1;
            this.openWalkableInterface = -1;
            this.continuedDialogue = false;
            this.currentTab = 3;
            this.inputDialogState = 0;
            this.menuOpen = false;
            this.messagePromptRaised = false;
            this.clickToContinueString = null;
            this.multicombat = 0;
            this.flashingSidebarId = -1;
            this.maleCharacter = true;
            this.resetCharacterDesign();

            for (int characterDesignColourIndex = 0; characterDesignColourIndex < 5; characterDesignColourIndex++) {
               this.characterDesignColours[characterDesignColourIndex] = 0;
            }

            for (int atPlayerActionIndex = 0; atPlayerActionIndex < 5; atPlayerActionIndex++) {
               this.atPlayerActions[atPlayerActionIndex] = null;
               this.atPlayerArray[atPlayerActionIndex] = false;
            }

            inventoryActionNoiseCounter = 0;
            keepaliveCounter = 0;
            playerActionNoiseCounter = 0;
            movementNoiseCounter = 0;
            objectActionNoiseCounter = 0;
            playerInteractionNoiseCounter = 0;
            audioNoiseCounter = 0;
            npcInteractionNoiseCounter = 0;
            this.setupGameScreenBuffers();
         } else if (sourceYCameraCurve == 3) {
            this.loginMessage1 = "";
            this.loginMessage2 = "Invalid username or password.";
         } else if (sourceYCameraCurve == 4) {
            this.inStream.currentPosition = 0;
            this.connection.flushInputStream(this.inStream.buffer, 2);
            sourceYCameraCurve = this.inStream.readUnsignedShort();
            this.loginMessage1 = "Your account has been disabled.";
            if (sourceYCameraCurve > 0) {
               this.loginMessage2 = "Your ban will be lifted in " + sourceYCameraCurve + " hours.";
            } else {
               this.loginMessage2 = "Your ban will last forever.";
            }
         } else if (sourceYCameraCurve == 5) {
            this.loginMessage1 = "Your account is already logged in.";
            this.loginMessage2 = "Try again in 60 secs...";
         } else if (sourceYCameraCurve == 6) {
            this.loginMessage1 = "RuneScape has been updated!";
            this.loginMessage2 = "Please download the latest client.";
         } else if (sourceYCameraCurve == 7) {
            this.loginMessage1 = "This world is full.";
            this.loginMessage2 = "Please use a different world.";
         } else if (sourceYCameraCurve == 8) {
            this.loginMessage1 = "Unable to connect.";
            this.loginMessage2 = "Login server offline.";
         } else if (sourceYCameraCurve == 9) {
            this.loginMessage1 = "Login limit exceeded.";
            this.loginMessage2 = "Too many connections from your address.";
         } else if (sourceYCameraCurve == 10) {
            this.loginMessage1 = "Unable to connect.";
            this.loginMessage2 = "Bad session id.";
         } else if (sourceYCameraCurve == 11) {
            this.loginMessage1 = "Login server rejected session.";
            this.loginMessage2 = "Please try again.";
         } else if (sourceYCameraCurve == 12) {
            this.loginMessage1 = "You need a members account to login to this world.";
            this.loginMessage2 = "Please select a different world.";
         } else if (sourceYCameraCurve == 29) {
            this.loginMessage1 = "Your account has been created.";
            this.loginMessage2 = "Only donators are able to play currently.";
         } else if (sourceYCameraCurve == 30) {
            this.loginMessage1 = "Only staff are allowed to play right now.";
            this.loginMessage2 = "Please login using a staff account.";
         } else if (sourceYCameraCurve == 13) {
            this.loginMessage1 = "Could not complete login.";
            this.loginMessage2 = "Please try using a different world.";
         } else if (sourceYCameraCurve == 14) {
            this.loginMessage1 = "The server is being updated.";
            this.loginMessage2 = "Please wait 1 minute and try again.";
         } else if (sourceYCameraCurve == 15) {
            loggedIn = true;
            this.outgoingBuffer.currentPosition = 0;
            this.inStream.currentPosition = 0;
            this.pktType = -1;
            this.lastOpcode = -1;
            this.prevPktType = -1;
            this.prevPktType2 = -1;
            this.pktSize = 0;
            this.timeoutCounter = 0;
            this.systemUpdateTime = 0;
            this.menuActionCount = 0;
            priorityMenuActionIndex = -1;
            this.menuOpen = false;
            this.lastRegionLoadActivityMillis = System.currentTimeMillis();
         } else if (sourceYCameraCurve == 16) {
            this.loginMessage1 = "Login attempts exceeded.";
            this.loginMessage2 = "Please wait 1 minute and try again.";
         } else if (sourceYCameraCurve == 17) {
            this.loginMessage1 = "You are standing in a members-only area.";
            this.loginMessage2 = "To play on this world move to a free area first";
         } else if (sourceYCameraCurve == 20) {
            this.loginMessage1 = "Invalid loginserver requested";
            this.loginMessage2 = "Please try using a different world.";
         } else if (sourceYCameraCurve != 21) {
            if (sourceYCameraCurve == -1) {
               if (scalar == 0) {
                  if (this.loginFailures < 2) {
                     try {
                        Thread.sleep(2000L);
                     } catch (Exception exception4) {
                     }

                     this.loginFailures++;
                     this.login(text, newText, flag);
                  } else {
                     this.loginMessage1 = "Error connecting to server.";
                     this.loginMessage2 = "Please try again in a little while.";
                  }
               } else {
                  this.loginMessage1 = "No response from server";
                  this.loginMessage2 = "Please try using a different world.";
               }
            } else {
               this.loginMessage1 = "Unexpected server response";
               this.loginMessage2 = "Please try using a different world.";
            }
         } else {
            for (int loopIndex6 = this.connection.read(); loopIndex6 >= 0; loopIndex6--) {
               this.loginMessage1 = "You have only just left another world";
               this.loginMessage2 = "Your profile will be transferred in: " + loopIndex6 + " seconds";
               this.drawLoginScreen(true);

               try {
                  Thread.sleep(1000L);
               } catch (Exception exception2) {
               }
            }

            this.login(text, newText, flag);
         }
      } catch (IOException exception3) {
         exception3.printStackTrace();
         this.loginMessage1 = "";
         this.loginMessage2 = "Error connecting to server.";
      }
   }
   private boolean doWalkTo(int scalarArgument, int newBigX, int sourceBigXIndex, int newBigY, int bigY2, int scalarArgument2, int blockingMask, int pathYEntry, int bigX2, boolean flag, int newIndex) {
      for (int pathIndex = 0; pathIndex < 104; pathIndex++) {
         for (int loopIndex = 0; loopIndex < 104; loopIndex++) {
            this.pathDirections[pathIndex][loopIndex] = 0;
            this.pathDistances[pathIndex][loopIndex] = 99999999;
         }
      }

      int pathDistanceIndex = bigX2;
      int bigYEntry = bigY2;
      this.pathDirections[bigX2][bigY2] = 99;
      this.pathDistances[bigX2][bigY2] = 0;
      int bigXIndex = 0;
      this.bigX[0] = bigX2;
      int bigXIndex2 = 1;
      this.bigY[0] = bigY2;
      boolean localFlag = false;
      int bigXLengthOrBigX = this.bigX.length;
      int[][] clippingData = this.collisionMaps[this.plane].clippingData;

      while (bigXIndex != bigXIndex2) {
         pathDistanceIndex = this.bigX[bigXIndex];
         bigYEntry = this.bigY[bigXIndex];
         bigXIndex = (bigXIndex + 1) % bigXLengthOrBigX;
         if (pathDistanceIndex == newIndex && bigYEntry == pathYEntry) {
            localFlag = true;
            break;
         }

         if (newBigY != 0) {
            if (newBigY < 5 || newBigY == 10) {
               CollisionMap collisionMap = this.collisionMaps[this.plane];
               int scalar = newBigY - 1;
               int scalar2 = pathYEntry;
               int scalar3 = scalar;
               int scalar4 = newBigX;
               int position = bigYEntry;
               int clippingDataIndex = pathDistanceIndex;
               int scalar5 = newIndex;
               CollisionMap sourceCollisionMap = collisionMap;
               boolean flag2;
               if (clippingDataIndex == scalar5 && position == scalar2) {
                  flag2 = true;
               } else {
                  checkWallReachability: {
                     if (scalar3 == 0) {
                        if (scalar4 == 0) {
                           if (clippingDataIndex == scalar5 - 1 && position == scalar2) {
                              flag2 = true;
                              break checkWallReachability;
                           }

                           if (clippingDataIndex == scalar5 && position == scalar2 + 1 && (sourceCollisionMap.clippingData[clippingDataIndex][position] & 19398944) == 0) {
                              flag2 = true;
                              break checkWallReachability;
                           }

                           if (clippingDataIndex == scalar5 && position == scalar2 - 1 && (sourceCollisionMap.clippingData[clippingDataIndex][position] & 19398914) == 0) {
                              flag2 = true;
                              break checkWallReachability;
                           }
                        } else if (scalar4 == 1) {
                           if (clippingDataIndex == scalar5 && position == scalar2 + 1) {
                              flag2 = true;
                              break checkWallReachability;
                           }

                           if (clippingDataIndex == scalar5 - 1 && position == scalar2 && (sourceCollisionMap.clippingData[clippingDataIndex][position] & 19398920) == 0) {
                              flag2 = true;
                              break checkWallReachability;
                           }

                           if (clippingDataIndex == scalar5 + 1 && position == scalar2 && (sourceCollisionMap.clippingData[clippingDataIndex][position] & 19399040) == 0) {
                              flag2 = true;
                              break checkWallReachability;
                           }
                        } else if (scalar4 == 2) {
                           if (clippingDataIndex == scalar5 + 1 && position == scalar2) {
                              flag2 = true;
                              break checkWallReachability;
                           }

                           if (clippingDataIndex == scalar5 && position == scalar2 + 1 && (sourceCollisionMap.clippingData[clippingDataIndex][position] & 19398944) == 0) {
                              flag2 = true;
                              break checkWallReachability;
                           }

                           if (clippingDataIndex == scalar5 && position == scalar2 - 1 && (sourceCollisionMap.clippingData[clippingDataIndex][position] & 19398914) == 0) {
                              flag2 = true;
                              break checkWallReachability;
                           }
                        } else if (scalar4 == 3) {
                           if (clippingDataIndex == scalar5 && position == scalar2 - 1) {
                              flag2 = true;
                              break checkWallReachability;
                           }

                           if (clippingDataIndex == scalar5 - 1 && position == scalar2 && (sourceCollisionMap.clippingData[clippingDataIndex][position] & 19398920) == 0) {
                              flag2 = true;
                              break checkWallReachability;
                           }

                           if (clippingDataIndex == scalar5 + 1 && position == scalar2 && (sourceCollisionMap.clippingData[clippingDataIndex][position] & 19399040) == 0) {
                              flag2 = true;
                              break checkWallReachability;
                           }
                        }
                     }

                     if (scalar3 == 2) {
                        if (scalar4 == 0) {
                           if (clippingDataIndex == scalar5 - 1 && position == scalar2) {
                              flag2 = true;
                              break checkWallReachability;
                           }

                           if (clippingDataIndex == scalar5 && position == scalar2 + 1) {
                              flag2 = true;
                              break checkWallReachability;
                           }

                           if (clippingDataIndex == scalar5 + 1 && position == scalar2 && (sourceCollisionMap.clippingData[clippingDataIndex][position] & 19399040) == 0) {
                              flag2 = true;
                              break checkWallReachability;
                           }

                           if (clippingDataIndex == scalar5 && position == scalar2 - 1 && (sourceCollisionMap.clippingData[clippingDataIndex][position] & 19398914) == 0) {
                              flag2 = true;
                              break checkWallReachability;
                           }
                        } else if (scalar4 == 1) {
                           if (clippingDataIndex == scalar5 - 1 && position == scalar2 && (sourceCollisionMap.clippingData[clippingDataIndex][position] & 19398920) == 0) {
                              flag2 = true;
                              break checkWallReachability;
                           }

                           if (clippingDataIndex == scalar5 && position == scalar2 + 1) {
                              flag2 = true;
                              break checkWallReachability;
                           }

                           if (clippingDataIndex == scalar5 + 1 && position == scalar2) {
                              flag2 = true;
                              break checkWallReachability;
                           }

                           if (clippingDataIndex == scalar5 && position == scalar2 - 1 && (sourceCollisionMap.clippingData[clippingDataIndex][position] & 19398914) == 0) {
                              flag2 = true;
                              break checkWallReachability;
                           }
                        } else if (scalar4 == 2) {
                           if (clippingDataIndex == scalar5 - 1 && position == scalar2 && (sourceCollisionMap.clippingData[clippingDataIndex][position] & 19398920) == 0) {
                              flag2 = true;
                              break checkWallReachability;
                           }

                           if (clippingDataIndex == scalar5 && position == scalar2 + 1 && (sourceCollisionMap.clippingData[clippingDataIndex][position] & 19398944) == 0) {
                              flag2 = true;
                              break checkWallReachability;
                           }

                           if (clippingDataIndex == scalar5 + 1 && position == scalar2) {
                              flag2 = true;
                              break checkWallReachability;
                           }

                           if (clippingDataIndex == scalar5 && position == scalar2 - 1) {
                              flag2 = true;
                              break checkWallReachability;
                           }
                        } else if (scalar4 == 3) {
                           if (clippingDataIndex == scalar5 - 1 && position == scalar2) {
                              flag2 = true;
                              break checkWallReachability;
                           }

                           if (clippingDataIndex == scalar5 && position == scalar2 + 1 && (sourceCollisionMap.clippingData[clippingDataIndex][position] & 19398944) == 0) {
                              flag2 = true;
                              break checkWallReachability;
                           }

                           if (clippingDataIndex == scalar5 + 1 && position == scalar2 && (sourceCollisionMap.clippingData[clippingDataIndex][position] & 19399040) == 0) {
                              flag2 = true;
                              break checkWallReachability;
                           }

                           if (clippingDataIndex == scalar5 && position == scalar2 - 1) {
                              flag2 = true;
                              break checkWallReachability;
                           }
                        }
                     }

                     if (scalar3 == 9) {
                        if (clippingDataIndex == scalar5 && position == scalar2 + 1 && (sourceCollisionMap.clippingData[clippingDataIndex][position] & 32) == 0) {
                           flag2 = true;
                           break checkWallReachability;
                        }

                        if (clippingDataIndex == scalar5 && position == scalar2 - 1 && (sourceCollisionMap.clippingData[clippingDataIndex][position] & 2) == 0) {
                           flag2 = true;
                           break checkWallReachability;
                        }

                        if (clippingDataIndex == scalar5 - 1 && position == scalar2 && (sourceCollisionMap.clippingData[clippingDataIndex][position] & 8) == 0) {
                           flag2 = true;
                           break checkWallReachability;
                        }

                        if (clippingDataIndex == scalar5 + 1 && position == scalar2 && (sourceCollisionMap.clippingData[clippingDataIndex][position] & 128) == 0) {
                           flag2 = true;
                           break checkWallReachability;
                        }
                     }

                     flag2 = false;
                  }
               }

               if (flag2) {
                  localFlag = true;
                  break;
               }
            }

            if (newBigY < 10) {
               CollisionMap collisionMap2 = this.collisionMaps[this.plane];
               int scalar6 = newBigY - 1;
               int sourcePathDistanceIndex = pathDistanceIndex;
               int scalar7 = newBigX;
               int scalar8 = scalar6;
               int position2 = bigYEntry;
               int scalar9 = pathYEntry;
               int scalar10 = newIndex;
               CollisionMap collisionMap3 = collisionMap2;
               boolean flag3;
               if (sourcePathDistanceIndex == scalar10 && position2 == scalar9) {
                  flag3 = true;
               } else {
                  checkWallDecorationReachability: {
                     if (scalar8 == 6 || scalar8 == 7) {
                        if (scalar8 == 7) {
                           scalar7 = scalar7 + 2 & 3;
                        }

                        if (scalar7 == 0) {
                           if (sourcePathDistanceIndex == scalar10 + 1 && position2 == scalar9 && (collisionMap3.clippingData[sourcePathDistanceIndex][position2] & 128) == 0) {
                              flag3 = true;
                              break checkWallDecorationReachability;
                           }

                           if (sourcePathDistanceIndex == scalar10 && position2 == scalar9 - 1 && (collisionMap3.clippingData[sourcePathDistanceIndex][position2] & 2) == 0) {
                              flag3 = true;
                              break checkWallDecorationReachability;
                           }
                        } else if (scalar7 == 1) {
                           if (sourcePathDistanceIndex == scalar10 - 1 && position2 == scalar9 && (collisionMap3.clippingData[sourcePathDistanceIndex][position2] & 8) == 0) {
                              flag3 = true;
                              break checkWallDecorationReachability;
                           }

                           if (sourcePathDistanceIndex == scalar10 && position2 == scalar9 - 1 && (collisionMap3.clippingData[sourcePathDistanceIndex][position2] & 2) == 0) {
                              flag3 = true;
                              break checkWallDecorationReachability;
                           }
                        } else if (scalar7 == 2) {
                           if (sourcePathDistanceIndex == scalar10 - 1 && position2 == scalar9 && (collisionMap3.clippingData[sourcePathDistanceIndex][position2] & 8) == 0) {
                              flag3 = true;
                              break checkWallDecorationReachability;
                           }

                           if (sourcePathDistanceIndex == scalar10 && position2 == scalar9 + 1 && (collisionMap3.clippingData[sourcePathDistanceIndex][position2] & 32) == 0) {
                              flag3 = true;
                              break checkWallDecorationReachability;
                           }
                        } else if (scalar7 == 3) {
                           if (sourcePathDistanceIndex == scalar10 + 1 && position2 == scalar9 && (collisionMap3.clippingData[sourcePathDistanceIndex][position2] & 128) == 0) {
                              flag3 = true;
                              break checkWallDecorationReachability;
                           }

                           if (sourcePathDistanceIndex == scalar10 && position2 == scalar9 + 1 && (collisionMap3.clippingData[sourcePathDistanceIndex][position2] & 32) == 0) {
                              flag3 = true;
                              break checkWallDecorationReachability;
                           }
                        }
                     }

                     if (scalar8 == 8) {
                        if (sourcePathDistanceIndex == scalar10 && position2 == scalar9 + 1 && (collisionMap3.clippingData[sourcePathDistanceIndex][position2] & 32) == 0) {
                           flag3 = true;
                           break checkWallDecorationReachability;
                        }

                        if (sourcePathDistanceIndex == scalar10 && position2 == scalar9 - 1 && (collisionMap3.clippingData[sourcePathDistanceIndex][position2] & 2) == 0) {
                           flag3 = true;
                           break checkWallDecorationReachability;
                        }

                        if (sourcePathDistanceIndex == scalar10 - 1 && position2 == scalar9 && (collisionMap3.clippingData[sourcePathDistanceIndex][position2] & 8) == 0) {
                           flag3 = true;
                           break checkWallDecorationReachability;
                        }

                        if (sourcePathDistanceIndex == scalar10 + 1 && position2 == scalar9 && (collisionMap3.clippingData[sourcePathDistanceIndex][position2] & 128) == 0) {
                           flag3 = true;
                           break checkWallDecorationReachability;
                        }
                     }

                     flag3 = false;
                  }
               }

               if (flag3) {
                  localFlag = true;
                  break;
               }
            }
         }

         if (scalarArgument2 != 0 && sourceBigXIndex != 0) {
            CollisionMap collisionMap4 = this.collisionMaps[this.plane];
            int position3 = bigYEntry;
            int scalar11 = scalarArgument2;
            int sourceBlockingMask = blockingMask;
            int scalar12 = sourceBigXIndex;
            int sourcePathDistanceIndex2 = pathDistanceIndex;
            int scalar13 = newIndex;
            int scalar14 = pathYEntry;
            CollisionMap collisionMap5 = collisionMap4;
            scalar11 = scalar13 + scalar11 - 1;
            scalar12 = scalar14 + scalar12 - 1;
            if (sourcePathDistanceIndex2 >= scalar13 && sourcePathDistanceIndex2 <= scalar11 && position3 >= scalar14 && position3 <= scalar12
               || sourcePathDistanceIndex2 == scalar13 - 1 && position3 >= scalar14 && position3 <= scalar12 && (collisionMap5.clippingData[sourcePathDistanceIndex2][position3] & 8) == 0 && (sourceBlockingMask & 8) == 0
               || sourcePathDistanceIndex2 == scalar11 + 1 && position3 >= scalar14 && position3 <= scalar12 && (collisionMap5.clippingData[sourcePathDistanceIndex2][position3] & 128) == 0 && (sourceBlockingMask & 2) == 0
               || position3 == scalar14 - 1 && sourcePathDistanceIndex2 >= scalar13 && sourcePathDistanceIndex2 <= scalar11 && (collisionMap5.clippingData[sourcePathDistanceIndex2][position3] & 2) == 0 && (sourceBlockingMask & 4) == 0
               || position3 == scalar12 + 1 && sourcePathDistanceIndex2 >= scalar13 && sourcePathDistanceIndex2 <= scalar11 && (collisionMap5.clippingData[sourcePathDistanceIndex2][position3] & 32) == 0 && (sourceBlockingMask & 1) == 0) {
               localFlag = true;
               break;
            }
         }

         int localPathDistances = this.pathDistances[pathDistanceIndex][bigYEntry] + 1;
         if (pathDistanceIndex > 0 && this.pathDirections[pathDistanceIndex - 1][bigYEntry] == 0 && (clippingData[pathDistanceIndex - 1][bigYEntry] & 19398920) == 0) {
            this.bigX[bigXIndex2] = pathDistanceIndex - 1;
            this.bigY[bigXIndex2] = bigYEntry;
            bigXIndex2 = (bigXIndex2 + 1) % bigXLengthOrBigX;
            this.pathDirections[pathDistanceIndex - 1][bigYEntry] = 2;
            this.pathDistances[pathDistanceIndex - 1][bigYEntry] = localPathDistances;
         }

         if (pathDistanceIndex < 103 && this.pathDirections[pathDistanceIndex + 1][bigYEntry] == 0 && (clippingData[pathDistanceIndex + 1][bigYEntry] & 19399040) == 0) {
            this.bigX[bigXIndex2] = pathDistanceIndex + 1;
            this.bigY[bigXIndex2] = bigYEntry;
            bigXIndex2 = (bigXIndex2 + 1) % bigXLengthOrBigX;
            this.pathDirections[pathDistanceIndex + 1][bigYEntry] = 8;
            this.pathDistances[pathDistanceIndex + 1][bigYEntry] = localPathDistances;
         }

         if (bigYEntry > 0 && this.pathDirections[pathDistanceIndex][bigYEntry - 1] == 0 && (clippingData[pathDistanceIndex][bigYEntry - 1] & 19398914) == 0) {
            this.bigX[bigXIndex2] = pathDistanceIndex;
            this.bigY[bigXIndex2] = bigYEntry - 1;
            bigXIndex2 = (bigXIndex2 + 1) % bigXLengthOrBigX;
            this.pathDirections[pathDistanceIndex][bigYEntry - 1] = 1;
            this.pathDistances[pathDistanceIndex][bigYEntry - 1] = localPathDistances;
         }

         if (bigYEntry < 103 && this.pathDirections[pathDistanceIndex][bigYEntry + 1] == 0 && (clippingData[pathDistanceIndex][bigYEntry + 1] & 19398944) == 0) {
            this.bigX[bigXIndex2] = pathDistanceIndex;
            this.bigY[bigXIndex2] = bigYEntry + 1;
            bigXIndex2 = (bigXIndex2 + 1) % bigXLengthOrBigX;
            this.pathDirections[pathDistanceIndex][bigYEntry + 1] = 4;
            this.pathDistances[pathDistanceIndex][bigYEntry + 1] = localPathDistances;
         }

         if (pathDistanceIndex > 0
            && bigYEntry > 0
            && this.pathDirections[pathDistanceIndex - 1][bigYEntry - 1] == 0
            && (clippingData[pathDistanceIndex - 1][bigYEntry - 1] & 19398926) == 0
            && (clippingData[pathDistanceIndex - 1][bigYEntry] & 19398920) == 0
            && (clippingData[pathDistanceIndex][bigYEntry - 1] & 19398914) == 0) {
            this.bigX[bigXIndex2] = pathDistanceIndex - 1;
            this.bigY[bigXIndex2] = bigYEntry - 1;
            bigXIndex2 = (bigXIndex2 + 1) % bigXLengthOrBigX;
            this.pathDirections[pathDistanceIndex - 1][bigYEntry - 1] = 3;
            this.pathDistances[pathDistanceIndex - 1][bigYEntry - 1] = localPathDistances;
         }

         if (pathDistanceIndex < 103
            && bigYEntry > 0
            && this.pathDirections[pathDistanceIndex + 1][bigYEntry - 1] == 0
            && (clippingData[pathDistanceIndex + 1][bigYEntry - 1] & 19399043) == 0
            && (clippingData[pathDistanceIndex + 1][bigYEntry] & 19399040) == 0
            && (clippingData[pathDistanceIndex][bigYEntry - 1] & 19398914) == 0) {
            this.bigX[bigXIndex2] = pathDistanceIndex + 1;
            this.bigY[bigXIndex2] = bigYEntry - 1;
            bigXIndex2 = (bigXIndex2 + 1) % bigXLengthOrBigX;
            this.pathDirections[pathDistanceIndex + 1][bigYEntry - 1] = 9;
            this.pathDistances[pathDistanceIndex + 1][bigYEntry - 1] = localPathDistances;
         }

         if (pathDistanceIndex > 0
            && bigYEntry < 103
            && this.pathDirections[pathDistanceIndex - 1][bigYEntry + 1] == 0
            && (clippingData[pathDistanceIndex - 1][bigYEntry + 1] & 19398968) == 0
            && (clippingData[pathDistanceIndex - 1][bigYEntry] & 19398920) == 0
            && (clippingData[pathDistanceIndex][bigYEntry + 1] & 19398944) == 0) {
            this.bigX[bigXIndex2] = pathDistanceIndex - 1;
            this.bigY[bigXIndex2] = bigYEntry + 1;
            bigXIndex2 = (bigXIndex2 + 1) % bigXLengthOrBigX;
            this.pathDirections[pathDistanceIndex - 1][bigYEntry + 1] = 6;
            this.pathDistances[pathDistanceIndex - 1][bigYEntry + 1] = localPathDistances;
         }

         if (pathDistanceIndex < 103
            && bigYEntry < 103
            && this.pathDirections[pathDistanceIndex + 1][bigYEntry + 1] == 0
            && (clippingData[pathDistanceIndex + 1][bigYEntry + 1] & 19399136) == 0
            && (clippingData[pathDistanceIndex + 1][bigYEntry] & 19399040) == 0
            && (clippingData[pathDistanceIndex][bigYEntry + 1] & 19398944) == 0) {
            this.bigX[bigXIndex2] = pathDistanceIndex + 1;
            this.bigY[bigXIndex2] = bigYEntry + 1;
            bigXIndex2 = (bigXIndex2 + 1) % bigXLengthOrBigX;
            this.pathDirections[pathDistanceIndex + 1][bigYEntry + 1] = 12;
            this.pathDistances[pathDistanceIndex + 1][bigYEntry + 1] = localPathDistances;
         }
      }

      this.alternativeRouteUsed = 0;
      if (!localFlag) {
         if (flag) {
            int pathDistances2 = 100;

            for (int loopIndex2 = 1; loopIndex2 < 2; loopIndex2++) {
               for (int pathIndex2 = newIndex - loopIndex2; pathIndex2 <= newIndex + loopIndex2; pathIndex2++) {
                  for (int loopIndex3 = pathYEntry - loopIndex2; loopIndex3 <= pathYEntry + loopIndex2; loopIndex3++) {
                     if (pathIndex2 >= 0 && loopIndex3 >= 0 && pathIndex2 < 104 && loopIndex3 < 104 && this.pathDistances[pathIndex2][loopIndex3] < pathDistances2) {
                        pathDistances2 = this.pathDistances[pathIndex2][loopIndex3];
                        pathDistanceIndex = pathIndex2;
                        bigYEntry = loopIndex3;
                        this.alternativeRouteUsed = 1;
                        localFlag = true;
                     }
                  }
               }

               if (localFlag) {
                  break;
               }
            }
         }

         if (!localFlag) {
            return false;
         }
      }

      this.bigX[0] = pathDistanceIndex;
      bigXIndex = 1;
      this.bigY[0] = bigYEntry;

      int position4;
      for (int position5 = position4 = this.pathDirections[pathDistanceIndex][bigYEntry]; pathDistanceIndex != bigX2 || bigYEntry != bigY2; position5 = this.pathDirections[pathDistanceIndex][bigYEntry]) {
         if (position5 != position4) {
            position4 = position5;
            this.bigX[bigXIndex] = pathDistanceIndex;
            this.bigY[bigXIndex++] = bigYEntry;
         }

         if ((position5 & 2) != 0) {
            pathDistanceIndex++;
         } else if ((position5 & 8) != 0) {
            pathDistanceIndex--;
         }

         if ((position5 & 1) != 0) {
            bigYEntry++;
         } else if ((position5 & 4) != 0) {
            bigYEntry--;
         }
      }

      if (bigXIndex > 0) {
         sourceBigXIndex = bigXIndex;
         if (bigXIndex > 25) {
            sourceBigXIndex = 25;
         }

         newBigX = this.bigX[--bigXIndex];
         newBigY = this.bigY[bigXIndex];
         if ((movementNoiseCounter += sourceBigXIndex) >= 92) {
            this.outgoingBuffer.writeOpcode(36);
            this.outgoingBuffer.writeInt(0);
            movementNoiseCounter = 0;
         }

         if (scalarArgument == 0) {
            this.outgoingBuffer.writeOpcode(164);
            this.outgoingBuffer.writeByte(sourceBigXIndex + sourceBigXIndex + 3);
         }

         if (scalarArgument == 1) {
            this.outgoingBuffer.writeOpcode(248);
            this.outgoingBuffer.writeByte(sourceBigXIndex + sourceBigXIndex + 3 + 14);
         }

         if (scalarArgument == 2) {
            this.outgoingBuffer.writeOpcode(98);
            this.outgoingBuffer.writeByte(sourceBigXIndex + sourceBigXIndex + 3);
         }

         this.outgoingBuffer.writeShortLittleEndianAdded(newBigX + this.baseX);
         this.destX = this.bigX[0];
         this.destY = this.bigY[0];

         for (int loopIndex4 = 1; loopIndex4 < sourceBigXIndex; loopIndex4++) {
            bigXIndex--;
            this.outgoingBuffer.writeByte(this.bigX[bigXIndex] - newBigX);
            this.outgoingBuffer.writeByte(this.bigY[bigXIndex] - newBigY);
         }

         this.outgoingBuffer.writeShortLittleEndian(newBigY + this.baseY);
         this.outgoingBuffer.writeByteNegated(super.keyStatus[5] != 1 ? 0 : 1);
         return true;
      } else {
         return scalarArgument != 1;
      }
   }
   private static int convertMidiVolumeToAttenuation(int currentMusicVolume) {
      return (int)(Math.log(currentMusicVolume * 0.00390625) * 868.5889638065036 + 0.5);
   }
   private void parseNpcUpdateMasks(Buffer buffer) {
      for (int entityUpdateIndex2 = 0; entityUpdateIndex2 < this.entityUpdateCount; entityUpdateIndex2++) {
         int entityUpdateIndex = this.entityUpdateIndices[entityUpdateIndex2];
         Npc npc;
         int npcIdOrSequences = (npc = this.npcs[entityUpdateIndex]).definition == null ? -1 : (int)npc.definition.id;
         int decodedUnsignedByte;
         if (((decodedUnsignedByte = buffer.readUnsignedByte()) & 16) != 0) {
            int emoteAnimationOrReadUnsignedShortLittleEndian;
             if ((emoteAnimationOrReadUnsignedShortLittleEndian = buffer.readUnsignedShortLittleEndian()) == 65535) {
                emoteAnimationOrReadUnsignedShortLittleEndian = -1;
             }

             int animationDelayOrReadUnsignedByte = buffer.readUnsignedByte();
             if (emoteAnimationOrReadUnsignedShortLittleEndian != -1) {
                int remappedId = AnimationSequence.remapId(emoteAnimationOrReadUnsignedShortLittleEndian, npcIdOrSequences);
                if (AnimationSequence.sequences == null
                   || remappedId < 0
                   || remappedId >= AnimationSequence.sequences.length
                   || AnimationSequence.sequences[remappedId] == null) {
                   System.err.println(
                      "Ignoring invalid NPC animation "
                         + emoteAnimationOrReadUnsignedShortLittleEndian
                         + " (resolved="
                         + remappedId
                         + ", npc="
                         + entityUpdateIndex
                         + ", definition="
                         + npcIdOrSequences
                         + ", sequenceCount="
                         + (AnimationSequence.sequences == null ? 0 : AnimationSequence.sequences.length)
                         + ")"
                   );
                   emoteAnimationOrReadUnsignedShortLittleEndian = -1;
                }
             }

             if (emoteAnimationOrReadUnsignedShortLittleEndian == npc.emoteAnimation && emoteAnimationOrReadUnsignedShortLittleEndian != -1) {
               if ((npcIdOrSequences = AnimationSequence.sequences[AnimationSequence.remapId(emoteAnimationOrReadUnsignedShortLittleEndian, npcIdOrSequences)].replyMode) == 1) {
                  npc.emoteFrame = 0;
                  npc.emoteFrameCycle = 0;
                  npc.animationDelay = animationDelayOrReadUnsignedByte;
                  npc.animationLoopCount = 0;
               }

               if (npcIdOrSequences == 2) {
                  npc.animationLoopCount = 0;
               }
            } else if (emoteAnimationOrReadUnsignedShortLittleEndian == -1
               || npc.emoteAnimation == -1
               || AnimationSequence.sequences[AnimationSequence.remapId(emoteAnimationOrReadUnsignedShortLittleEndian, npcIdOrSequences)].forcedPriority >= AnimationSequence.sequences[AnimationSequence.remapId(npc.emoteAnimation, npcIdOrSequences)].forcedPriority) {
               npc.emoteAnimation = emoteAnimationOrReadUnsignedShortLittleEndian;
               npc.emoteFrame = 0;
               npc.emoteFrameCycle = 0;
               npc.animationDelay = animationDelayOrReadUnsignedByte;
               npc.animationLoopCount = 0;
               npc.emotePathLength = npc.pathLength;
            }
         }

         if ((decodedUnsignedByte & 8) != 0) {
            int damage = buffer.readUnsignedByteAdded();
            int hitType = buffer.readUnsignedByteNegated();
            npc.addHit(hitType, damage, 0, gameCycle);
            npc.healthBarEndCycle = gameCycle + 300;
            npc.currentHealth = buffer.readUnsignedByteAdded();
            npc.maxHealth = buffer.readUnsignedByte();
         }

         if ((decodedUnsignedByte & 128) != 0) {
            npc.graphicId = buffer.readUnsignedShort();
            int graphicHeightOrReadInt = buffer.readInt();
            npc.graphicHeight = graphicHeightOrReadInt >> 16;
            npc.graphicDelay = gameCycle + (graphicHeightOrReadInt & 65535);
            npc.graphicFrame = 0;
            npc.graphicFrameCycle = 0;
            if (npc.graphicDelay > gameCycle) {
               npc.graphicFrame = -1;
            }

            if (npc.graphicId == 65535) {
               npc.graphicId = -1;
            }

            npcIdOrSequences = -1;
            if (npc.graphicId != -1) {
               npcIdOrSequences = AnimationSequence.remapId(SpotAnimationDefinition.definitions[npc.graphicId].animationSequence.frameIds[0] >> 16);
            }

            if ((hdModels || !hdModels && extendedRevisionEnabled && (npcIdOrSequences > 1043 || use2007Models || extendedModelIds.contains(npcIdOrSequences))) && npcIdOrSequences != -1) {
               try {
                  if (AnimationFrame.frameCache.get(npcIdOrSequences) == null) {
                     this.onDemandFetcher.provide(1, npcIdOrSequences);
                  }
               } catch (Exception exception) {
               }
            }
         }

         if ((decodedUnsignedByte & 32) != 0) {
            npc.interactingEntity = buffer.readUnsignedShort();
            if (npc.interactingEntity == 65535) {
               npc.interactingEntity = -1;
            }
         }

         if ((decodedUnsignedByte & 1) != 0) {
            npc.spokenText = buffer.readString();
            npc.textCycle = 100;
         }

         if ((decodedUnsignedByte & 64) != 0) {
            int damage = buffer.readUnsignedByteNegated();
            int hitType = buffer.readUnsignedByteSubtracted();
            npc.addHit(hitType, damage, 0, gameCycle);
            npc.healthBarEndCycle = gameCycle + 300;
            npc.currentHealth = buffer.readUnsignedByteSubtracted();
            npc.maxHealth = buffer.readUnsignedByteNegated();
         }

         if ((decodedUnsignedByte & 2) != 0) {
            npc.definition = NpcDefinition.lookup(buffer.readUnsignedShortLittleEndianAdded());
            npc.size = npc.definition.size;
            npc.turnSpeed = npc.definition.turnSpeed;
            npc.walkAnimationId = npc.definition.walkAnimationId;
            npc.turnAroundAnimationId = npc.definition.turnAroundAnimationId;
            npc.turnRightAnimationId = npc.definition.turnRightAnimationId;
            npc.turnLeftAnimationId = npc.definition.turnLeftAnimationId;
            npc.idleAnimationId = npc.definition.idleAnimationId;
         }

         if ((decodedUnsignedByte & 4) != 0) {
            npc.faceX = buffer.readUnsignedShortLittleEndian();
            npc.faceY = buffer.readUnsignedShortLittleEndian();
         }
      }
   }
   private void buildAtNPCMenu(NpcDefinition npcDefinition, int newMenuParam2, int newMenuParam1, int newMenuParam0) {
      if (this.menuActionCount < 400) {
         if (npcDefinition.childIds != null) {
            npcDefinition = npcDefinition.morph();
         }

         if (npcDefinition != null && npcDefinition.clickable) {
            String text = npcDefinition.name;
            if (npcDefinition.combatLevel != 0) {
               text = text + combatDiffColor(localPlayer.combatLevel, npcDefinition.combatLevel) + " (level-" + npcDefinition.combatLevel + ")";
            }

            if (this.itemSelected == 1) {
               this.menuActionNames[this.menuActionCount] = "Use " + this.selectedItemName + " with @yel@" + text;
               this.menuActionIds[this.menuActionCount] = 582;
               this.menuParam2[this.menuActionCount] = newMenuParam2;
               this.menuParam0[this.menuActionCount] = newMenuParam0;
               this.menuParam1[this.menuActionCount] = newMenuParam1;
               this.menuActionCount++;
               return;
            }

            if (this.spellSelected == 1) {
               if ((this.spellUsableOn & 2) == 2) {
                  this.menuActionNames[this.menuActionCount] = this.spellTooltip + " @yel@" + text;
                  this.menuActionIds[this.menuActionCount] = 413;
                  this.menuParam2[this.menuActionCount] = newMenuParam2;
                  this.menuParam0[this.menuActionCount] = newMenuParam0;
                  this.menuParam1[this.menuActionCount] = newMenuParam1;
                  this.menuActionCount++;
                  return;
               }
            } else {
               if (npcDefinition.actions != null) {
                  for (int actionIndex2 = 4; actionIndex2 >= 0; actionIndex2--) {
                     if (npcDefinition.actions[actionIndex2] != null && !npcDefinition.actions[actionIndex2].equalsIgnoreCase("attack")) {
                        this.menuActionNames[this.menuActionCount] = npcDefinition.actions[actionIndex2] + " @yel@" + text;
                        if (actionIndex2 == 0) {
                           this.menuActionIds[this.menuActionCount] = 20;
                        }

                        if (actionIndex2 == 1) {
                           this.menuActionIds[this.menuActionCount] = 412;
                        }

                        if (actionIndex2 == 2) {
                           this.menuActionIds[this.menuActionCount] = 225;
                        }

                        if (actionIndex2 == 3) {
                           this.menuActionIds[this.menuActionCount] = 965;
                        }

                        if (actionIndex2 == 4) {
                           this.menuActionIds[this.menuActionCount] = 478;
                        }

                        this.menuParam2[this.menuActionCount] = newMenuParam2;
                        this.menuParam0[this.menuActionCount] = newMenuParam0;
                        this.menuParam1[this.menuActionCount] = newMenuParam1;
                        this.menuActionCount++;
                     }
                  }
               }

               if (npcDefinition.actions != null) {
                  for (int actionIndex = 4; actionIndex >= 0; actionIndex--) {
                     if (npcDefinition.actions[actionIndex] != null && npcDefinition.actions[actionIndex].equalsIgnoreCase("attack")) {
                        short menuActionId = 0;
                        if (npcDefinition.combatLevel > localPlayer.combatLevel && attackOption == 0 || attackOption == 2) {
                           menuActionId = 2000;
                        }

                        this.menuActionNames[this.menuActionCount] = npcDefinition.actions[actionIndex] + " @yel@" + text;
                        if (actionIndex == 0) {
                           this.menuActionIds[this.menuActionCount] = menuActionId + 20;
                        }

                        if (actionIndex == 1) {
                           this.menuActionIds[this.menuActionCount] = menuActionId + 412;
                        }

                        if (actionIndex == 2) {
                           this.menuActionIds[this.menuActionCount] = menuActionId + 225;
                        }

                        if (actionIndex == 3) {
                           this.menuActionIds[this.menuActionCount] = menuActionId + 965;
                        }

                        if (actionIndex == 4) {
                           this.menuActionIds[this.menuActionCount] = menuActionId + 478;
                        }

                        this.menuParam2[this.menuActionCount] = newMenuParam2;
                        this.menuParam0[this.menuActionCount] = newMenuParam0;
                        this.menuParam1[this.menuActionCount] = newMenuParam1;
                        this.menuActionCount++;
                     }
                  }
               }

               this.menuActionNames[this.menuActionCount] = "Examine @yel@" + text;
               this.menuActionIds[this.menuActionCount] = 1025;
               this.menuParam2[this.menuActionCount] = newMenuParam2;
               this.menuParam0[this.menuActionCount] = newMenuParam0;
               this.menuParam1[this.menuActionCount] = newMenuParam1;
               this.menuActionCount++;
            }
         }
      }
   }
   private static String buildChatNameIcons(int chatPrivilege, int chatDonatorStatuse, int chatAccountMode) {
      String text = "";
      if (chatPrivilege == 1) {
         text = text + "<img=0>";
      }

      if (chatPrivilege == 2) {
         text = text + "<img=1>";
      }

      if (chatAccountMode == 1 && chatPrivilege < 2) {
         text = text + "<img=3>";
      }

      if (chatAccountMode == 2 && chatPrivilege < 2) {
         text = text + "<img=4>";
      }

      if (chatAccountMode == 3 && chatPrivilege < 2) {
         text = text + "<img=5>";
      }

      if (chatDonatorStatuse == 1 && chatPrivilege < 2) {
         text = text + "<img=2>";
      }

      return text;
   }
   private void buildAtPlayerMenu(int newMenuParam0, int newMenuParam2, Player player, int newMenuParam1) {
      if (player != localPlayer && this.menuActionCount < 400) {
         String text;
         if (player.skill == 0) {
            text = player.name + combatDiffColor(localPlayer.combatLevel, player.combatLevel) + " (level-" + player.combatLevel + ")";
         } else {
            text = player.name + " (skill-" + player.skill + ")";
         }

         text = buildChatNameIcons(player.privelage, player.donatorStatus, player.accountMode) + text;
         if (this.itemSelected == 1) {
            this.menuActionNames[this.menuActionCount] = "Use " + this.selectedItemName + " with @whi@" + text;
            this.menuActionIds[this.menuActionCount] = 491;
            this.menuParam2[this.menuActionCount] = newMenuParam2;
            this.menuParam0[this.menuActionCount] = newMenuParam0;
            this.menuParam1[this.menuActionCount] = newMenuParam1;
            this.menuActionCount++;
         } else if (this.spellSelected == 1) {
            if ((this.spellUsableOn & 8) == 8) {
               this.menuActionNames[this.menuActionCount] = this.spellTooltip + " @whi@" + text;
               this.menuActionIds[this.menuActionCount] = 365;
               this.menuParam2[this.menuActionCount] = newMenuParam2;
               this.menuParam0[this.menuActionCount] = newMenuParam0;
               this.menuParam1[this.menuActionCount] = newMenuParam1;
               this.menuActionCount++;
            }
         } else {
            for (int atPlayerActionIndex = 4; atPlayerActionIndex >= 0; atPlayerActionIndex--) {
               if (this.atPlayerActions[atPlayerActionIndex] != null) {
                  boolean attackAction = this.atPlayerActions[atPlayerActionIndex].equalsIgnoreCase("attack");
                  if (attackAction
                     && localPlayer.team != 0
                     && player.team != 0
                     && localPlayer.team == player.team) {
                     continue;
                  }

                  this.menuActionNames[this.menuActionCount] = this.atPlayerActions[atPlayerActionIndex] + " @whi@" + text;
                  short menuActionId = 0;
                  if (attackAction) {
                     if (player.combatLevel > localPlayer.combatLevel) {
                        menuActionId = 2000;
                     }

                     if (localPlayer.team != 0 && player.team != 0) {
                        if (localPlayer.team == player.team) {
                           menuActionId = 2000;
                        } else {
                           menuActionId = 0;
                        }
                     }
                  } else if (this.atPlayerArray[atPlayerActionIndex]) {
                     menuActionId = 2000;
                  }

                  if (atPlayerActionIndex == 0) {
                     this.menuActionIds[this.menuActionCount] = menuActionId + 561;
                  }

                  if (atPlayerActionIndex == 1) {
                     this.menuActionIds[this.menuActionCount] = menuActionId + 779;
                  }

                  if (atPlayerActionIndex == 2) {
                     this.menuActionIds[this.menuActionCount] = menuActionId + 27;
                  }

                  if (atPlayerActionIndex == 3) {
                     this.menuActionIds[this.menuActionCount] = menuActionId + 577;
                  }

                  if (atPlayerActionIndex == 4) {
                     this.menuActionIds[this.menuActionCount] = menuActionId + 729;
                  }

                  this.menuParam2[this.menuActionCount] = newMenuParam2;
                  this.menuParam0[this.menuActionCount] = newMenuParam0;
                  this.menuParam1[this.menuActionCount] = newMenuParam1;
                  this.menuActionCount++;
               }
            }
         }

         for (int menuActionIdIndex = 0; menuActionIdIndex < this.menuActionCount; menuActionIdIndex++) {
            if (this.menuActionIds[menuActionIdIndex] == 516) {
               this.menuActionNames[menuActionIdIndex] = "Walk here @whi@" + text;
               return;
            }
         }
      }
   }
   private void captureSpawnedObjectState(SpawnedObject spawnedObject) {
      int localScene = 0;
      int sourcePreviousId = -1;
      int sourcePreviousType = 0;
      int scene2 = 0;
      if (spawnedObject.group == 0) {
         localScene = this.scene.getWallHash(spawnedObject.plane, spawnedObject.x, spawnedObject.y);
      }

      if (spawnedObject.group == 1) {
         localScene = this.scene.getWallDecorationHash(spawnedObject.plane, spawnedObject.x, spawnedObject.y);
      }

      if (spawnedObject.group == 2) {
         localScene = this.scene.getInteractiveObjectHash(spawnedObject.plane, spawnedObject.x, spawnedObject.y);
      }

      if (spawnedObject.group == 3) {
         localScene = this.scene.getFloorDecorationHash(spawnedObject.plane, spawnedObject.x, spawnedObject.y);
      }

      if (localScene != 0) {
         scene2 = this.scene.getArrangement(spawnedObject.plane, spawnedObject.x, spawnedObject.y, localScene);
         sourcePreviousId = localScene >> 14 & 32767;
         sourcePreviousType = scene2 & 31;
         scene2 >>= 6;
      }

      spawnedObject.previousId = sourcePreviousId;
      spawnedObject.previousType = sourcePreviousType;
      spawnedObject.previousOrientation = scene2;
   }
   private void processAudioQueue() {
      for (int soundVolumeIndex = 0; this.currentSound > soundVolumeIndex; soundVolumeIndex++) {
         this.soundVolume[soundVolumeIndex]--;
         if (this.soundVolume[soundVolumeIndex] >= -10) {
            SoundEffect soundEffect;
            if ((soundEffect = queuedSoundEffects[soundVolumeIndex]) == null) {
               if ((soundEffect = SoundEffect.effects[this.sound[soundVolumeIndex]]) == null) {
                  continue;
               }

               this.soundVolume[soundVolumeIndex] = this.soundVolume[soundVolumeIndex] + soundEffect.trim();
               queuedSoundEffects[soundVolumeIndex] = soundEffect;
            }

            if (this.soundVolume[soundVolumeIndex] < 0) {
               RawSound rawSound = soundEffect.toRawSound();
               AudioResampler selectedResampler = audioResampler;
               RawSound sourceRawSound = rawSound;
               rawSound.samples = selectedResampler.resample(sourceRawSound.samples);
               sourceRawSound.sampleRate = selectedResampler.scaleRate(sourceRawSound.sampleRate);
               if (sourceRawSound.start == sourceRawSound.end) {
                  sourceRawSound.start = sourceRawSound.end = selectedResampler.scalePosition(sourceRawSound.start);
               } else {
                  sourceRawSound.start = selectedResampler.scalePosition(sourceRawSound.start);
                  sourceRawSound.end = selectedResampler.scalePosition(sourceRawSound.end);
                  if (sourceRawSound.start == sourceRawSound.end) {
                     sourceRawSound.start--;
                  }
               }

               RawPcmStream rawPcmStream;
               (rawPcmStream = RawPcmStream.create(sourceRawSound, 100, soundEffectVolume)).setNumLoops(this.soundType[soundVolumeIndex] - 1);
               pcmStreamMixer.addSubStream(rawPcmStream);
               this.soundVolume[soundVolumeIndex] = -100;
            }
         } else {
            this.currentSound--;

            for (int soundIndex = soundVolumeIndex; this.currentSound > soundIndex; soundIndex++) {
               this.sound[soundIndex] = this.sound[soundIndex + 1];
               queuedSoundEffects[soundIndex] = queuedSoundEffects[soundIndex + 1];
               this.soundType[soundIndex] = this.soundType[soundIndex + 1];
               this.soundVolume[soundIndex] = this.soundVolume[soundIndex + 1];
            }

            soundVolumeIndex--;
         }
      }

      if (this.previousSong > 0) {
         this.previousSong -= 20;
         if (this.previousSong < 0) {
            this.previousSong = 0;
         }

         if (this.previousSong == 0 && musicVolumeSetting != 0 && this.currentSong != -1) {
            this.requestMusicTrackImmediate(musicVolumeSetting, this.currentSong);
         }
      }
   }
   @Override
   final void startUp() {
      this.drawLoadingText(20, "Starting up");
      startupInitialized = true;
      if (SignLink.cacheDataFile != null) {
         for (int cacheStoreIndex = 0; cacheStoreIndex < cacheStoreCount; cacheStoreIndex++) {
            this.cacheStores[cacheStoreIndex] = new CacheStore(SignLink.cacheDataFile, SignLink.cacheIndexFiles[cacheStoreIndex], cacheStoreIndex + 1);
         }
      }

      try {
         this.titleArchive = this.loadArchive(1, "title screen", "title", this.archiveCrcs[1], 25);
         this.smallFont = new BitmapFont(false, "p11_full", this.titleArchive);
         this.plainFont = new BitmapFont(false, "p12_full", this.titleArchive);
         this.boldFont = new BitmapFont(false, "b12_full", this.titleArchive);
         new BitmapFont(true, "q8_full", this.titleArchive);
         this.richSmallFont = new RichTextFont(false, "p11_full", this.titleArchive);
         this.richPlainFont = new RichTextFont(false, "p12_full", this.titleArchive);
         this.richBoldFont = new RichTextFont(false, "b12_full", this.titleArchive);
         this.richQuillFont = new RichTextFont(true, "q8_full", this.titleArchive);
         this.drawLogo();
         this.loadTitleScreen();
         initializeMidiPlayer();
         pcmStreamMixer = createPcmStreamMixer(clientInstance);
         audioResampler = new AudioResampler(22050, audioSampleRate);
         Archive archive = this.loadArchive(2, "config", "config", this.archiveCrcs[2], 30);
         Archive loadArchiveResult = this.loadArchive(3, "interface", "interface", this.archiveCrcs[3], 35);
         Archive archive2 = this.loadArchive(4, "2d graphics", "media", this.archiveCrcs[4], 40);
         Archive archive3 = this.loadArchive(6, "textures", "textures", this.archiveCrcs[6], 45);
         Archive archive4 = this.loadArchive(7, "chat system", "wordenc", this.archiveCrcs[7], 50);
         Archive archive5 = this.loadArchive(8, "sound effects", "sounds", this.archiveCrcs[8], 55);
         this.byteGroundArray = new byte[4][104][104];
         this.intGroundArray = new int[4][105][105];
         this.scene = new SceneGraph(this.intGroundArray);

         for (int collisionMapIndex = 0; collisionMapIndex < 4; collisionMapIndex++) {
            this.collisionMaps[collisionMapIndex] = new CollisionMap();
         }

         this.minimapImage = new Sprite(512, 512);
         Archive archive6 = this.loadArchive(5, "update list", "versionlist", this.archiveCrcs[5], 60);
         this.drawLoadingText(60, "Connecting to update server");
         this.onDemandFetcher = new OnDemandFetcher();
         this.onDemandFetcher.start(archive6, this);
         if (!hdModels) {
            AnimationFrame.initialize(this.onDemandFetcher.getAnimCount());
         }

         Model.initializeModelCache(this.onDemandFetcher.getVersionCount(0), this.onDemandFetcher);
         this.updateSeasonalTheme();
         this.requestMusicTrackWithFade(18, musicVolumeSetting, this.customSettingShowExperiencePerHourStartLevels);

         while (this.onDemandFetcher.getNodeCount() > 0) {
            this.processOnDemandQueue();

            try {
               Thread.sleep(100L);
            } catch (Exception exception) {
            }

            if (this.onDemandFetcher.connectionErrors > 3) {
               this.showOnDemandLoadError();
               return;
            }
         }

         if (!hdModels) {
            this.drawLoadingText(65, "Requesting animations");
            int versionCount = this.onDemandFetcher.getVersionCount(1);

            for (int versionIndex = 0; versionIndex < versionCount; versionIndex++) {
               this.onDemandFetcher.provide(1, versionIndex);
            }

            while (this.onDemandFetcher.getNodeCount() > 0) {
               int scalar;
               if ((scalar = versionCount - this.onDemandFetcher.getNodeCount()) > 0) {
                  this.drawLoadingText(65, "Loading animations - " + scalar * 100 / versionCount + "%");
               }

               this.processOnDemandQueue();

               try {
                  Thread.sleep(100L);
               } catch (Exception exception10) {
               }

               if (this.onDemandFetcher.connectionErrors > 3) {
                  this.showOnDemandLoadError();
                  return;
               }
            }
         }

         this.drawLoadingText(70, "Requesting models");
         int onDemandFetcher2 = this.onDemandFetcher.getVersionCount(0);

         for (int modelIndex = 0; modelIndex < onDemandFetcher2; modelIndex++) {
            if ((this.onDemandFetcher.getModelIndex(modelIndex) & 1) != 0) {
               this.onDemandFetcher.provide(0, modelIndex);
            }
         }

         onDemandFetcher2 = this.onDemandFetcher.getNodeCount();

         while (this.onDemandFetcher.getNodeCount() > 0) {
            int scalar2;
            if ((scalar2 = onDemandFetcher2 - this.onDemandFetcher.getNodeCount()) > 0) {
               this.drawLoadingText(70, "Loading models - " + scalar2 * 100 / onDemandFetcher2 + "%");
            }

            this.processOnDemandQueue();

            try {
               Thread.sleep(100L);
            } catch (Exception exception2) {
            }
         }

         if (this.cacheStores[0] != null) {
            this.drawLoadingText(75, "Requesting maps");
            this.onDemandFetcher.provide(3, this.onDemandFetcher.getMapFileId(0, 48, 47));
            this.onDemandFetcher.provide(3, this.onDemandFetcher.getMapFileId(1, 48, 47));
            this.onDemandFetcher.provide(3, this.onDemandFetcher.getMapFileId(0, 48, 48));
            this.onDemandFetcher.provide(3, this.onDemandFetcher.getMapFileId(1, 48, 48));
            this.onDemandFetcher.provide(3, this.onDemandFetcher.getMapFileId(0, 48, 49));
            this.onDemandFetcher.provide(3, this.onDemandFetcher.getMapFileId(1, 48, 49));
            this.onDemandFetcher.provide(3, this.onDemandFetcher.getMapFileId(0, 47, 47));
            this.onDemandFetcher.provide(3, this.onDemandFetcher.getMapFileId(1, 47, 47));
            this.onDemandFetcher.provide(3, this.onDemandFetcher.getMapFileId(0, 47, 48));
            this.onDemandFetcher.provide(3, this.onDemandFetcher.getMapFileId(1, 47, 48));
            this.onDemandFetcher.provide(3, this.onDemandFetcher.getMapFileId(0, 148, 48));
            this.onDemandFetcher.provide(3, this.onDemandFetcher.getMapFileId(1, 148, 48));
            onDemandFetcher2 = this.onDemandFetcher.getNodeCount();

            while (this.onDemandFetcher.getNodeCount() > 0) {
               int scalar3;
               if ((scalar3 = onDemandFetcher2 - this.onDemandFetcher.getNodeCount()) > 0) {
                  this.drawLoadingText(75, "Loading maps - " + scalar3 * 100 / onDemandFetcher2 + "%");
               }

               this.processOnDemandQueue();

               try {
                  Thread.sleep(100L);
               } catch (Exception exception3) {
               }
            }
         }

         onDemandFetcher2 = this.onDemandFetcher.getVersionCount(0);

         for (int modelIndex2 = 0; modelIndex2 < onDemandFetcher2; modelIndex2++) {
            int onDemandFetcher3 = this.onDemandFetcher.getModelIndex(modelIndex2);
            byte byteCode = 0;
            if ((onDemandFetcher3 & 8) != 0) {
               byteCode = 10;
            } else if ((onDemandFetcher3 & 32) != 0) {
               byteCode = 9;
            } else if ((onDemandFetcher3 & 16) != 0) {
               byteCode = 8;
            } else if ((onDemandFetcher3 & 64) != 0) {
               byteCode = 7;
            } else if ((onDemandFetcher3 & 128) != 0) {
               byteCode = 6;
            } else if ((onDemandFetcher3 & 2) != 0) {
               byteCode = 5;
            } else if ((onDemandFetcher3 & 4) != 0) {
               byteCode = 4;
            }

            if ((onDemandFetcher3 & 1) != 0) {
               byteCode = 3;
            }

            if (byteCode != 0) {
               this.onDemandFetcher.readCacheFile(0, modelIndex2);
            }
         }

         this.onDemandFetcher.prefetchMaps(isMembers);
         int onDemandFetcher4 = this.onDemandFetcher.getVersionCount(2);

         for (int midiIndex = 1; midiIndex < onDemandFetcher4; midiIndex++) {
            if (this.onDemandFetcher.shouldPreloadMidi(midiIndex)) {
               this.onDemandFetcher.readCacheFile(2, midiIndex);
            }
         }

         this.drawLoadingText(80, "Unpacking media");
         this.invBack = new IndexedSprite(archive2, "invback", 0);
         this.chatBack = new IndexedSprite(archive2, "chatback", 0);

         for (int mapBackSpriteIndex = 0; mapBackSpriteIndex < 2; mapBackSpriteIndex++) {
            mapBackSprites[mapBackSpriteIndex] = new IndexedSprite(archive2, "mapback", mapBackSpriteIndex);
         }

         mapBack = mapBackSprites[0];
         this.backBase1 = new IndexedSprite(archive2, "backbase1", 0);
         this.backBase2 = new IndexedSprite(archive2, "backbase2", 0);
         this.backHmid1 = new IndexedSprite(archive2, "backhmid1", 0);

         for (int sideIconIndex = 0; sideIconIndex < 13; sideIconIndex++) {
            this.sideIcons[sideIconIndex] = new IndexedSprite(archive2, "sideicons", sideIconIndex);
         }

         defaultCompassSprite = compassSprite = new Sprite(archive2, "compass", 0);
         this.mapEdge = new Sprite(archive2, "mapedge", 0);
         this.mapEdge.expandToCanvas();
         this.multiOverlay = new Sprite(archive2, "overlay_multiway", 0);

         try {
            for (int mapSceneSpriteIndex = 0; mapSceneSpriteIndex < 100; mapSceneSpriteIndex++) {
               this.mapSceneSprites[mapSceneSpriteIndex] = new IndexedSprite(archive2, "mapscene", mapSceneSpriteIndex);
            }
         } catch (Exception exception4) {
         }

         try {
            for (int mapFunctionIndex = 0; mapFunctionIndex < 100; mapFunctionIndex++) {
               this.mapFunctions[mapFunctionIndex] = new Sprite(archive2, "mapfunction", mapFunctionIndex);
            }
         } catch (Exception exception5) {
         }

         try {
            for (int hitMarkIndex = 0; hitMarkIndex < 20; hitMarkIndex++) {
               this.hitMarks[hitMarkIndex] = new Sprite(archive2, "hitmarks", hitMarkIndex);
            }
         } catch (Exception exception6) {
         }

         try {
            for (int headIconsHintIndex = 0; headIconsHintIndex < 6; headIconsHintIndex++) {
               this.headIconsHint[headIconsHintIndex] = new Sprite(archive2, "headicons_hint", headIconsHintIndex);
            }

            for (int headIconIndex = 0; headIconIndex < 8; headIconIndex++) {
               this.headIcons[headIconIndex] = new Sprite(archive2, "headicons_prayer", headIconIndex);
            }

            for (int skullIconIndex = 0; skullIconIndex < 3; skullIconIndex++) {
               this.skullIcons[skullIconIndex] = new Sprite(archive2, "headicons_pk", skullIconIndex);
            }
         } catch (Exception exception7) {
         }

         try {
            int position = 0;
            position++;
            miscInterfaceSprites[0] = new Sprite(archive2, "tradebacking", 0);

            for (int loopIndex = 0; loopIndex < 4; loopIndex++) {
               miscInterfaceSprites[position++] = new Sprite(archive2, "steelborder", loopIndex);
            }

            for (int loopIndex2 = 0; loopIndex2 < 2; loopIndex2++) {
               miscInterfaceSprites[position++] = new Sprite(archive2, "steelborder2", loopIndex2);
            }

            for (int loopIndex3 = 2; loopIndex3 < 4; loopIndex3++) {
               miscInterfaceSprites[position++] = new Sprite(archive2, "miscgraphics", loopIndex3);
            }
         } catch (Exception exception8) {
         }

         this.mapFlag = new Sprite(archive2, "mapmarker", 0);
         this.mapMarker = new Sprite(archive2, "mapmarker", 1);

         for (int crosseIndex = 0; crosseIndex < 8; crosseIndex++) {
            this.crosses[crosseIndex] = new Sprite(archive2, "cross", crosseIndex);
         }

         this.skillIconSprites[0] = new Sprite(archive2, "staticons", 0);
         this.skillIconSprites[1] = new Sprite(archive2, "staticons", 2);
         this.skillIconSprites[2] = new Sprite(archive2, "staticons", 1);
         this.skillIconSprites[3] = new Sprite(archive2, "staticons", 6);
         this.skillIconSprites[4] = new Sprite(archive2, "staticons", 3);
         this.skillIconSprites[5] = new Sprite(archive2, "staticons", 4);
         this.skillIconSprites[6] = new Sprite(archive2, "staticons", 5);
         this.skillIconSprites[7] = new Sprite(archive2, "staticons", 15);
         this.skillIconSprites[8] = new Sprite(archive2, "staticons", 17);
         this.skillIconSprites[9] = new Sprite(archive2, "staticons", 11);
         this.skillIconSprites[10] = new Sprite(archive2, "staticons", 14);
         this.skillIconSprites[11] = new Sprite(archive2, "staticons", 16);
         this.skillIconSprites[12] = new Sprite(archive2, "staticons", 10);
         this.skillIconSprites[13] = new Sprite(archive2, "staticons", 13);
         this.skillIconSprites[14] = new Sprite(archive2, "staticons", 12);
         this.skillIconSprites[15] = new Sprite(archive2, "staticons", 8);
         this.skillIconSprites[16] = new Sprite(archive2, "staticons", 7);
         this.skillIconSprites[17] = new Sprite(archive2, "staticons", 9);
         this.skillIconSprites[18] = new Sprite(archive2, "staticons2", 1);
         this.skillIconSprites[19] = new Sprite(archive2, "staticons2", 2);
         this.skillIconSprites[20] = new Sprite(archive2, "staticons2", 0);
         this.mapDotItem = new Sprite(archive2, "mapdots", 0);
         this.mapDotNpc = new Sprite(archive2, "mapdots", 1);
         this.mapDotNPC = new Sprite(archive2, "mapdots", 2);
         this.mapDotPlayer = new Sprite(archive2, "mapdots", 3);
         this.mapDotFriend = new Sprite(archive2, "mapdots", 4);
         this.mapDotTeam = new Sprite(archive2, "mod_icons", 1);
         this.scrollBar1 = new IndexedSprite(archive2, "scrollbar", 0);
         this.scrollBar2 = new IndexedSprite(archive2, "scrollbar", 1);
         this.redStone1 = new IndexedSprite(archive2, "redstone1", 0);
         this.redStone2 = new IndexedSprite(archive2, "redstone2", 0);
         this.redStone3 = new IndexedSprite(archive2, "redstone3", 0);
         this.redStone1_2 = new IndexedSprite(archive2, "redstone1", 0);
         this.redStone1_2.flipHorizontal();
         this.redStone2_2 = new IndexedSprite(archive2, "redstone2", 0);
         this.redStone2_2.flipHorizontal();
         this.redStone1_3 = new IndexedSprite(archive2, "redstone1", 0);
         this.redStone1_3.flipVertical();
         this.redStone2_3 = new IndexedSprite(archive2, "redstone2", 0);
         this.redStone2_3.flipVertical();
         this.redStone3_2 = new IndexedSprite(archive2, "redstone3", 0);
         this.redStone3_2.flipVertical();
         this.redStone1_4 = new IndexedSprite(archive2, "redstone1", 0);
         this.redStone1_4.flipHorizontal();
         this.redStone1_4.flipVertical();
         this.redStone2_4 = new IndexedSprite(archive2, "redstone2", 0);
         this.redStone2_4.flipHorizontal();
         this.redStone2_4.flipVertical();

         for (int moderatorIconIndex = 0; moderatorIconIndex < 3; moderatorIconIndex++) {
            this.moderatorIcons[moderatorIconIndex] = new IndexedSprite(archive2, "mod_icons", moderatorIconIndex);
         }

         for (int gameModeIconIndex = 0; gameModeIconIndex < 4; gameModeIconIndex++) {
            this.gameModeIcons[gameModeIconIndex] = new IndexedSprite(archive2, "gamemode_icons", gameModeIconIndex);
         }

         for (int chatIconSpriteIndex = 0; chatIconSpriteIndex < 3; chatIconSpriteIndex++) {
            this.chatIconSprites[chatIconSpriteIndex] = new Sprite(archive2, "mod_icons", chatIconSpriteIndex);
         }

         for (int loopIndex4 = 0; loopIndex4 < 4; loopIndex4++) {
            this.chatIconSprites[loopIndex4 + 3] = new Sprite(archive2, "gamemode_icons", loopIndex4);
         }

         Sprite[] iconsOrChatIconSprites = this.chatIconSprites;
         RichTextFont richTextFont = this.richSmallFont;
         this.richSmallFont.icons = iconsOrChatIconSprites;
         Sprite[] chatIconSprites = this.chatIconSprites;
         RichTextFont richPlainFont = this.richPlainFont;
         this.richPlainFont.icons = chatIconSprites;
         Sprite[] iconsOrChatIconSprites2 = this.chatIconSprites;
         RichTextFont richTextFont2 = this.richBoldFont;
         this.richBoldFont.icons = iconsOrChatIconSprites2;
         Sprite[] iconsOrChatIconSprites3 = this.chatIconSprites;
         RichTextFont richTextFont3 = this.richQuillFont;
         this.richQuillFont.icons = iconsOrChatIconSprites3;
         Sprite sprite = new Sprite(archive2, "backleft1", 0);
         int spriteWidth = sprite.spriteWidth;
         int spriteHeight = sprite.spriteHeight;
         this.getGameComponent();
         this.backVmidIP2_2 = new BufferedImageGraphicsBuffer(spriteWidth, spriteHeight);
         if (screenMode == 0) {
            sprite.drawOpaqueSprite(0, 0);
         }

         backLeft2Sprite = new Sprite(archive2, "backleft2", 0);
         backRight1Sprite = new Sprite(archive2, "backright1", 0);
         backRight2Sprite = new Sprite(archive2, "backright2", 0);
         backTop1Sprite = new Sprite(archive2, "backtop1", 0);
         backVmid1Sprite = new Sprite(archive2, "backvmid1", 0);
         backVmid2Sprite = new Sprite(archive2, "backvmid2", 0);
         backVmid3Sprite = new Sprite(archive2, "backvmid3", 0);
         backHmid2Sprite = new Sprite(archive2, "backhmid2", 0);
         int scalar4 = (int)(Math.random() * 21.0) - 10;
         int scalar5 = (int)(Math.random() * 21.0) - 10;
         onDemandFetcher2 = (int)(Math.random() * 21.0) - 10;
         int scalar6 = (int)(Math.random() * 41.0) - 20;

         for (int mapFunctionIndex2 = 0; mapFunctionIndex2 < 100; mapFunctionIndex2++) {
            if (this.mapFunctions[mapFunctionIndex2] != null) {
               this.mapFunctions[mapFunctionIndex2].adjustRgb(scalar4 + scalar6, scalar5 + scalar6, onDemandFetcher2 + scalar6);
            }

            if (this.mapSceneSprites[mapFunctionIndex2] != null) {
               this.mapSceneSprites[mapFunctionIndex2].adjustPalette(scalar4 + scalar6, scalar5 + scalar6, onDemandFetcher2 + scalar6);
            }
         }

         this.drawLoadingText(83, "Unpacking textures");
         Rasterizer3D.loadTextures(archive3);
         Rasterizer3D.setBrightness(0.8);
         Rasterizer3D.initializeTextureCache();
         this.drawLoadingText(86, "Unpacking config");
         AnimationSequence.load(archive);
         ObjectDefinition.unpackConfig(archive);
         FloorDefinition.unpackConfig(archive);
         ItemDefinition.unpackConfig(archive);
         NpcDefinition.unpackConfig(archive);
         IdentityKit.unpackConfig(archive);
         SpotAnimationDefinition.unpackConfig(archive);
         VarpDefinition.load(archive);
         VarbitDefinition.load(archive);
         ItemDefinition.membersServer = isMembers;
         this.drawLoadingText(90, "Unpacking sounds");
         byte[] file = archive5.getFile("sounds.dat");
         SoundEffect.load(new Buffer(file));
         this.drawLoadingText(95, "Unpacking interfaces");
         RichTextFont[] values = new RichTextFont[]{this.richSmallFont, this.richPlainFont, this.richBoldFont, this.richQuillFont};
         Widget.load(loadArchiveResult, values, archive2);
         this.drawLoadingText(100, "Preparing game engine");
         calculateMinimapMasks();
         updateRasterizerBounds();
         int[] integerBuffer = new int[9];

         for (int loopIndex5 = 0; loopIndex5 < 9; loopIndex5++) {
            int sINEIndex = 128 + (loopIndex5 << 5) + 15;
            int scalar7 = 600 + sINEIndex * 3;
            int sINEEntry = Rasterizer3D.SINE[sINEIndex];
            integerBuffer[loopIndex5] = scalar7 * sINEEntry >> 16;
         }

         SceneGraph.precalculateTileVisibility(500, 800, 512, 334, integerBuffer);
         ChatFilter.load(archive4);
         this.mouseRecorder = new MouseRecorder(this);
         this.startRunnable(this.mouseRecorder, 10);
         DynamicObject.clientInstance = this;
         ObjectDefinition.clientInstance = this;
         NpcDefinition.clientInstance = this;
         applyGameframeVersion();
         new DefaultClientBootstrap();
      } catch (Exception exception9) {
         exception9.printStackTrace();
         SignLink.reporterror("loaderror " + this.loadingStatusText + " " + this.loadingErrorCode);
         this.loadingError = true;
      }
   }
   private static void updateRasterizerBounds() {
      Rasterizer3D.setViewport(screenMode == 0 ? 765 : clientWidth, screenMode == 0 ? 503 : clientHeight);
      fullScreenScanOffsets = Rasterizer3D.scanOffsets;
      if (gameframeVersion != 474) {
         Rasterizer3D.setViewport(classicChatRasterWidth, classicChatRasterHeight);
      } else {
         Rasterizer3D.setViewport(modernChatRasterWidth, modernChatRasterHeight);
      }

      chatboxScanOffsets = Rasterizer3D.scanOffsets;
      if (gameframeVersion != 474) {
         Rasterizer3D.setViewport(musicVolume, 261);
      } else {
         Rasterizer3D.setViewport(modernSidebarRasterWidth, 261);
      }

      sidebarScanOffsets = Rasterizer3D.scanOffsets;
      Rasterizer3D.setViewport(512, 334);
      viewportScanOffsets = Rasterizer3D.scanOffsets;
      if (gameframeVersion == 474) {
         activeChatRasterWidth = modernChatRasterWidth;
         activeChatRasterHeight = modernChatRasterHeight;
         activeSidebarRasterWidth = modernSidebarRasterWidth;
      } else {
         activeChatRasterWidth = classicChatRasterWidth;
         activeChatRasterHeight = classicChatRasterHeight;
         activeSidebarRasterWidth = musicVolume;
      }
   }
   private static void calculateMinimapMasks() {
      for (int compassMaskLineOffsetIndex = 0; compassMaskLineOffsetIndex < 33; compassMaskLineOffsetIndex++) {
         int compassMaskLineOffset = 999;
         int compassMaskLineWidth = 0;

         for (int loopIndex = 0; loopIndex < 34; loopIndex++) {
            if (mapBack.pixelIndices[loopIndex + compassMaskLineOffsetIndex * mapBack.width] == 0) {
               if (compassMaskLineOffset == 999) {
                  compassMaskLineOffset = loopIndex;
               }
            } else if (compassMaskLineOffset != 999) {
               compassMaskLineWidth = loopIndex;
               break;
            }
         }

         compassMaskLineOffsets[compassMaskLineOffsetIndex] = compassMaskLineOffset;
         compassMaskLineWidths[compassMaskLineOffsetIndex] = compassMaskLineWidth - compassMaskLineOffset;
      }

      for (int loopIndex2 = 5; loopIndex2 < 156; loopIndex2++) {
         int minimapMaskLineOffset = 999;
         int minimapMaskLineWidth = 0;

         for (int loopIndex3 = 25; loopIndex3 < 172; loopIndex3++) {
            if (mapBack.pixelIndices[loopIndex3 + loopIndex2 * mapBack.width] != 0 || loopIndex3 <= 34 && loopIndex2 <= 34) {
               if (minimapMaskLineOffset != 999) {
                  minimapMaskLineWidth = loopIndex3;
                  break;
               }
            } else if (minimapMaskLineOffset == 999) {
               minimapMaskLineOffset = loopIndex3;
            }
         }

         minimapMaskLineOffsets[loopIndex2 - 5] = minimapMaskLineOffset - 25;
         minimapMaskLineWidths[loopIndex2 - 5] = minimapMaskLineWidth - minimapMaskLineOffset;
      }
   }
   public static void applyGameframeVersion() {
      if (gameframeVersion == 317) {
         compassSprite = defaultCompassSprite;
      } else {
         compassSprite = customSprites[0];
      }

      if (gameframeVersion == 474) {
         mapBack = mapBackSprites[1];
      } else {
         mapBack = mapBackSprites[0];
      }

      if (gameframeVersion != 474) {
         chatViewMode = 0;
         tiara = 0;
         orbsEnabled = false;
      }

      Client client;
      (client = getClient()).graphicsBuffer = null;
      client.chatboxImageProducer = null;
      client.minimapImageProducer = null;
      client.tabImageProducer = null;
      client.gameScreenImageProducer = null;
      client.bottomFrameStripBuffer = null;
      client.bottomRightFrameStripBuffer = null;
      client.topRightFrameStripBuffer = null;
      client.rightFrameStripBuffer = null;
      client.middleRightFrameStripBuffer = null;
      client.chatRightFrameStripBuffer = null;
      client.chatTopFrameStripBuffer = null;
      client.chatLeftFrameStripBuffer = null;
      client.minimapRightFrameStripBuffer = null;
      client.minimapLeftFrameStripBuffer = null;
      client.chatSettingImageProducer = null;
      if (gameframeVersion == 474 && orbsEnabled) {
         client.getGameComponent();
         client.minimapImageProducer = new BufferedImageGraphicsBuffer(201, 156);
      } else {
         client.getGameComponent();
         client.minimapImageProducer = new BufferedImageGraphicsBuffer(172, 156);
      }

      client.getGameComponent();
      int gameRasterWidth = screenMode == 0 ? 512 : clientWidth;
      int gameRasterHeight = screenMode == 0 ? 334 : clientHeight;
      client.gameScreenImageProducer = new BufferedImageGraphicsBuffer(gameRasterWidth, gameRasterHeight);
      calculateMinimapMasks();
      updateRasterizerBounds();

      try {
         saveDisplaySettings();
      } catch (IOException exception) {
         exception.printStackTrace();
      }
   }
   private void updateOtherPlayerMovement(Buffer buffer, int packetSize) {
      int playerIndex;
      while (buffer.bitPosition + 10 < packetSize << 3 && (playerIndex = buffer.readBits(11)) != 2047) {
         if (this.players[playerIndex] == null) {
            this.players[playerIndex] = new Player();
            if (this.playerAppearanceBuffers[playerIndex] != null) {
               this.players[playerIndex].updatePlayer(this.playerAppearanceBuffers[playerIndex]);
            }
         }

         this.playerIndices[this.playerCount++] = playerIndex;
         Player player;
         (player = this.players[playerIndex]).lastUpdateCycle = gameCycle;
         if (buffer.readBits(1) == 1) {
            this.entityUpdateIndices[this.entityUpdateCount++] = playerIndex;
         }

         playerIndex = buffer.readBits(1);
         int decodedBits;
         if ((decodedBits = buffer.readBits(5)) > 15) {
            decodedBits -= 32;
         }

         int readBits2;
         if ((readBits2 = buffer.readBits(5)) > 15) {
            readBits2 -= 32;
         }

         player.setPosition(localPlayer.pathX[0] + readBits2, localPlayer.pathY[0] + decodedBits, playerIndex == 1);
      }

      buffer.finishBitAccess();
   }
   private static String formatInterfaceValue(int scalarArgument) {
      return scalarArgument < 999999999 ? String.valueOf(scalarArgument) : "*";
   }

   @Override
   public URL getCodeBase() {
      try {
         return new URL("http://" + serverAddress + ":80");
      } catch (Exception exception) {
         return null;
      }
   }
   private void updateNpcInstances() {
      for (int npcIndex2 = 0; npcIndex2 < this.npcCount; npcIndex2++) {
         int npcIndex = this.npcIndices[npcIndex2];
         Npc npc;
         if ((npc = this.npcs[npcIndex]) != null) {
            this.updateActor(npc);
         }
      }
   }
   private void updateActor(Actor actor) {
      if (actor.worldX < 128 || actor.worldY < 128 || actor.worldX >= 13184 || actor.worldY >= 13184) {
         actor.emoteAnimation = -1;
         actor.graphicId = -1;
         actor.forceMoveStartCycle = 0;
         actor.forceMoveEndCycle = 0;
         actor.worldX = (actor.pathX[0] << 7) + (actor.size << 6);
         actor.worldY = (actor.pathY[0] << 7) + (actor.size << 6);
         actor.resetPath();
      }

      if (actor == localPlayer && (actor.worldX < 1536 || actor.worldY < 1536 || actor.worldX >= 11776 || actor.worldY >= 11776)) {
         actor.emoteAnimation = -1;
         actor.graphicId = -1;
         actor.forceMoveStartCycle = 0;
         actor.forceMoveEndCycle = 0;
         actor.worldX = (actor.pathX[0] << 7) + (actor.size << 6);
         actor.worldY = (actor.pathY[0] << 7) + (actor.size << 6);
         actor.resetPath();
      }

      if (actor.forceMoveStartCycle > gameCycle) {
         Actor sourceActor = actor;
         int localForceMoveStartCycle = actor.forceMoveStartCycle - gameCycle;
         int scalar = (sourceActor.forceMoveStartX << 7) + (sourceActor.size << 6);
         int scalar2 = (sourceActor.forceMoveStartY << 7) + (sourceActor.size << 6);
         sourceActor.worldX = sourceActor.worldX + (scalar - sourceActor.worldX) / localForceMoveStartCycle;
         sourceActor.worldY = sourceActor.worldY + (scalar2 - sourceActor.worldY) / localForceMoveStartCycle;
         sourceActor.updateCounter = 0;
         if (sourceActor.forceMoveFaceDirection == 0) {
            sourceActor.targetOrientation = 1024;
         }

         if (sourceActor.forceMoveFaceDirection == 1) {
            sourceActor.targetOrientation = 1536;
         }

         if (sourceActor.forceMoveFaceDirection == 2) {
            sourceActor.targetOrientation = 0;
         }

         if (sourceActor.forceMoveFaceDirection == 3) {
            sourceActor.targetOrientation = 512;
         }
      } else if (actor.forceMoveEndCycle >= gameCycle) {
         Actor actor2 = actor;
         int npcId = -1;
         if (actor2 instanceof Npc) {
            Npc npc;
            npcId = (int)(npc = (Npc)actor2).definition.id;
         }

         if (actor2.forceMoveEndCycle == gameCycle
            || actor2.emoteAnimation == -1
            || actor2.animationDelay != 0
            || actor2.emoteFrameCycle + 1 > AnimationSequence.sequences[AnimationSequence.remapId(actor2.emoteAnimation, npcId)].getFrameLength(actor2.emoteFrame)) {
            int localForceMoveEndCycle = actor2.forceMoveEndCycle - actor2.forceMoveStartCycle;
            int localGameCycle = gameCycle - actor2.forceMoveStartCycle;
            npcId = (actor2.forceMoveStartX << 7) + (actor2.size << 6);
            int scalar3 = (actor2.forceMoveStartY << 7) + (actor2.size << 6);
            int scalar4 = (actor2.forceMoveEndX << 7) + (actor2.size << 6);
            int scalar5 = (actor2.forceMoveEndY << 7) + (actor2.size << 6);
            actor2.worldX = (npcId * (localForceMoveEndCycle - localGameCycle) + scalar4 * localGameCycle) / localForceMoveEndCycle;
            actor2.worldY = (scalar3 * (localForceMoveEndCycle - localGameCycle) + scalar5 * localGameCycle) / localForceMoveEndCycle;
         }

         actor2.updateCounter = 0;
         if (actor2.forceMoveFaceDirection == 0) {
            actor2.targetOrientation = 1024;
         }

         if (actor2.forceMoveFaceDirection == 1) {
            actor2.targetOrientation = 1536;
         }

         if (actor2.forceMoveFaceDirection == 2) {
            actor2.targetOrientation = 0;
         }

         if (actor2.forceMoveFaceDirection == 3) {
            actor2.targetOrientation = 512;
         }

         actor2.orientation = actor2.targetOrientation;
      } else {
         Actor actor3 = actor;
         int sourceWorldX = -1;
         if (actor3 instanceof Npc) {
            Npc npc4;
            sourceWorldX = (int)(npc4 = (Npc)actor3).definition.id;
         }

         actor3.movementAnimation = actor3.idleAnimationId;
         if (actor3.pathLength == 0) {
            actor3.updateCounter = 0;
         } else {
            processActorMovement: {
               if (actor3.emoteAnimation != -1 && actor3.animationDelay == 0) {
                  AnimationSequence sequence = AnimationSequence.sequences[AnimationSequence.remapId(actor3.emoteAnimation, sourceWorldX)];
                  if (actor3.emotePathLength > 0 && sequence.precedenceAnimating == 0) {
                     actor3.updateCounter++;
                     break processActorMovement;
                  }

                  if (actor3.emotePathLength <= 0 && sequence.priority == 0) {
                     actor3.updateCounter++;
                     break processActorMovement;
                  }
               }

               int worldX = actor3.worldX;
               int worldY = actor3.worldY;
               sourceWorldX = (actor3.pathX[actor3.pathLength - 1] << 7) + (actor3.size << 6);
               int deltaYOrWorldY = (actor3.pathY[actor3.pathLength - 1] << 7) + (actor3.size << 6);
               if (sourceWorldX - worldX <= 256 && sourceWorldX - worldX >= -256 && deltaYOrWorldY - worldY <= 256 && deltaYOrWorldY - worldY >= -256) {
                  if (worldX < sourceWorldX) {
                     if (worldY < deltaYOrWorldY) {
                        actor3.targetOrientation = 1280;
                     } else if (worldY > deltaYOrWorldY) {
                        actor3.targetOrientation = 1792;
                     } else {
                        actor3.targetOrientation = 1536;
                     }
                  } else if (worldX > sourceWorldX) {
                     if (worldY < deltaYOrWorldY) {
                        actor3.targetOrientation = 768;
                     } else if (worldY > deltaYOrWorldY) {
                        actor3.targetOrientation = 256;
                     } else {
                        actor3.targetOrientation = 512;
                     }
                  } else if (worldY < deltaYOrWorldY) {
                     actor3.targetOrientation = 1024;
                  } else {
                     actor3.targetOrientation = 0;
                  }

                  int scalar6;
                  if ((scalar6 = actor3.targetOrientation - actor3.orientation & 2047) > 1024) {
                     scalar6 -= 2048;
                  }

                  int movementAnimationOrTurnAroundAnimationId = actor3.turnAroundAnimationId;
                  if (scalar6 >= -256 && scalar6 <= 256) {
                     movementAnimationOrTurnAroundAnimationId = actor3.walkAnimationId;
                  } else if (scalar6 >= 256 && scalar6 < 768) {
                     movementAnimationOrTurnAroundAnimationId = actor3.turnLeftAnimationId;
                  } else if (scalar6 >= -768 && scalar6 <= -256) {
                     movementAnimationOrTurnAroundAnimationId = actor3.turnRightAnimationId;
                  }

                  if (movementAnimationOrTurnAroundAnimationId == -1) {
                     movementAnimationOrTurnAroundAnimationId = actor3.walkAnimationId;
                  }

                  actor3.movementAnimation = movementAnimationOrTurnAroundAnimationId;
                  byte byteCode = 4;
                  if (actor3.orientation != actor3.targetOrientation && actor3.interactingEntity == -1 && actor3.turnSpeed != 0) {
                     byteCode = 2;
                  }

                  if (actor3.pathLength > 2) {
                     byteCode = 6;
                  }

                  if (actor3.pathLength > 3) {
                     byteCode = 8;
                  }

                  if (actor3.updateCounter > 0 && actor3.pathLength > 1) {
                     byteCode = 8;
                     actor3.updateCounter--;
                  }

                  if (actor3.pathRun[actor3.pathLength - 1]) {
                     byteCode <<= 1;
                  }

                  if (byteCode >= 8 && actor3.movementAnimation == actor3.walkAnimationId && actor3.runAnimationId != -1) {
                     actor3.movementAnimation = actor3.runAnimationId;
                  }

                  if (worldX < sourceWorldX) {
                     actor3.worldX += byteCode;
                     if (actor3.worldX > sourceWorldX) {
                        actor3.worldX = sourceWorldX;
                     }
                  } else if (worldX > sourceWorldX) {
                     actor3.worldX -= byteCode;
                     if (actor3.worldX < sourceWorldX) {
                        actor3.worldX = sourceWorldX;
                     }
                  }

                  if (worldY < deltaYOrWorldY) {
                     actor3.worldY += byteCode;
                     if (actor3.worldY > deltaYOrWorldY) {
                        actor3.worldY = deltaYOrWorldY;
                     }
                  } else if (worldY > deltaYOrWorldY) {
                     actor3.worldY -= byteCode;
                     if (actor3.worldY < deltaYOrWorldY) {
                        actor3.worldY = deltaYOrWorldY;
                     }
                  }

                  if (actor3.worldX == sourceWorldX && actor3.worldY == deltaYOrWorldY) {
                     actor3.pathLength--;
                     if (actor3.emotePathLength > 0) {
                        actor3.emotePathLength--;
                     }
                  }
               } else {
                  actor3.worldX = sourceWorldX;
                  actor3.worldY = deltaYOrWorldY;
               }
            }
         }
      }

      Actor actor4 = actor;
      Client client = this;
      if (actor4.turnSpeed != 0) {
         Npc npc2;
         if (actor4.interactingEntity != -1 && actor4.interactingEntity < 32768 && (npc2 = client.npcs[actor4.interactingEntity]) != null) {
            int worldX2 = actor4.worldX - npc2.worldX;
            int worldY2 = actor4.worldY - npc2.worldY;
            if (worldX2 != 0 || worldY2 != 0) {
               actor4.targetOrientation = (int)(Math.atan2(worldX2, worldY2) * 325.949) & 2047;
            }
         }

         if (actor4.interactingEntity >= 32768) {
            int playerIndex;
            if ((playerIndex = actor4.interactingEntity - 32768) == client.localPlayerIndex) {
               playerIndex = 2047;
            }

            Player player;
            if ((player = client.players[playerIndex]) != null) {
               int worldX3 = actor4.worldX - player.worldX;
               int worldY3 = actor4.worldY - player.worldY;
               if (worldX3 != 0 || worldY3 != 0) {
                  actor4.targetOrientation = (int)(Math.atan2(worldX3, worldY3) * 325.949) & 2047;
               }
            }
         }

         if ((actor4.faceX != 0 || actor4.faceY != 0) && (actor4.pathLength == 0 || actor4.updateCounter > 0)) {
            int worldX4 = actor4.worldX - (actor4.faceX - client.baseX - client.baseX << 6);
            int worldY4 = actor4.worldY - (actor4.faceY - client.baseY - client.baseY << 6);
            if (worldX4 != 0 || worldY4 != 0) {
               actor4.targetOrientation = (int)(Math.atan2(worldX4, worldY4) * 325.949) & 2047;
            }

            actor4.faceX = 0;
            actor4.faceY = 0;
         }

         int targetOrientation2;
         if ((targetOrientation2 = actor4.targetOrientation - actor4.orientation & 2047) != 0) {
            if (targetOrientation2 < actor4.turnSpeed || targetOrientation2 > 2048 - actor4.turnSpeed) {
               actor4.orientation = actor4.targetOrientation;
            } else if (targetOrientation2 > 1024) {
               actor4.orientation = actor4.orientation - actor4.turnSpeed;
            } else {
               actor4.orientation = actor4.orientation + actor4.turnSpeed;
            }

            actor4.orientation &= 2047;
            if (actor4.movementAnimation == actor4.idleAnimationId && actor4.orientation != actor4.targetOrientation) {
               if (actor4.standTurnAnimationId != -1) {
                  actor4.movementAnimation = actor4.standTurnAnimationId;
               } else {
                  actor4.movementAnimation = actor4.walkAnimationId;
               }
            }
         }
      }

      Actor actor5 = actor;
      actor.animationStretches = false;
      int localNpcId = -1;
      if (actor5 instanceof Npc) {
         Npc npc3;
         localNpcId = (int)(npc3 = (Npc)actor5).definition.id;
      }

      if (actor5.movementAnimation != -1) {
         AnimationSequence animationSequence2 = AnimationSequence.sequences[AnimationSequence.remapId(actor5.movementAnimation, localNpcId)];
         actor5.movementFrameCycle++;
         if (actor5.movementFrame < animationSequence2.frameCount && actor5.movementFrameCycle > animationSequence2.getFrameLength(actor5.movementFrame)) {
            actor5.movementFrameCycle = 1;
            actor5.movementFrame++;
            actor5.nextMovementFrame++;
         }

         if (smoothAnimations) {
            actor5.nextMovementFrame = actor5.movementFrame + 1;
         }

         if (actor5.nextMovementFrame >= animationSequence2.frameCount) {
            actor5.nextMovementFrame = 0;
         }

         if (actor5.movementFrame >= animationSequence2.frameCount) {
            actor5.movementFrameCycle = 0;
            actor5.movementFrame = 0;
         }
      }

      if (actor5.graphicId != -1 && gameCycle >= actor5.graphicDelay) {
         if (actor5.graphicFrame < 0) {
            actor5.graphicFrame = 0;
         }

         AnimationSequence animationSequence3 = SpotAnimationDefinition.definitions[actor5.graphicId].animationSequence;
         actor5.graphicFrameCycle++;

         while (actor5.graphicFrame < animationSequence3.frameCount && actor5.graphicFrameCycle > animationSequence3.getFrameLength(actor5.graphicFrame)) {
            actor5.graphicFrameCycle = actor5.graphicFrameCycle - animationSequence3.getFrameLength(actor5.graphicFrame);
            actor5.graphicFrame++;
         }

         if (actor5.graphicFrame >= animationSequence3.frameCount && (actor5.graphicFrame < 0 || actor5.graphicFrame >= animationSequence3.frameCount)) {
            actor5.graphicId = -1;
         }

         if (smoothAnimations) {
            actor5.nextGraphicFrame = actor5.graphicFrame + 1;
            if (actor5.nextGraphicFrame >= animationSequence3.frameCount && (actor5.nextGraphicFrame < 0 || actor5.nextGraphicFrame >= animationSequence3.frameCount)) {
               actor5.graphicId = -1;
            }
         }
      }

      if (actor5.emoteAnimation != -1
         && actor5.animationDelay <= 1
         && AnimationSequence.sequences[AnimationSequence.remapId(actor5.emoteAnimation, localNpcId)].precedenceAnimating == 1
         && actor5.emotePathLength > 0
         && actor5.forceMoveStartCycle <= gameCycle
         && actor5.forceMoveEndCycle < gameCycle) {
         actor5.animationDelay = 1;
      } else {
         if (actor5.emoteAnimation != -1 && actor5.animationDelay == 0) {
            AnimationSequence animationSequence4 = AnimationSequence.sequences[AnimationSequence.remapId(actor5.emoteAnimation, localNpcId)];
            actor5.emoteFrameCycle++;

            while (actor5.emoteFrame < animationSequence4.frameCount && actor5.emoteFrameCycle > animationSequence4.getFrameLength(actor5.emoteFrame)) {
               actor5.emoteFrameCycle = actor5.emoteFrameCycle - animationSequence4.getFrameLength(actor5.emoteFrame);
               actor5.emoteFrame++;
            }

            if (actor5.emoteFrame >= animationSequence4.frameCount) {
               actor5.emoteFrame = actor5.emoteFrame - animationSequence4.frameStep;
               actor5.animationLoopCount++;
               if (actor5.animationLoopCount >= animationSequence4.maxLoops) {
                  actor5.emoteAnimation = -1;
               }

               if (actor5.emoteFrame < 0 || actor5.emoteFrame >= animationSequence4.frameCount) {
                  actor5.emoteAnimation = -1;
               }
            }

            if (smoothAnimations) {
               actor5.nextEmoteFrame = actor5.emoteFrame + 1;
               if (actor5.nextEmoteFrame >= animationSequence4.frameCount) {
                  if (actor5.animationLoopCount >= animationSequence4.maxLoops) {
                     actor5.nextEmoteFrame = actor5.emoteFrame + 1;
                  }

                  if (actor5.nextEmoteFrame < 0 || actor5.nextEmoteFrame >= animationSequence4.frameCount) {
                     actor5.nextEmoteFrame = actor5.emoteFrame;
                  }
               }
            }

            actor5.animationStretches = animationSequence4.stretches;
         }

         if (actor5.animationDelay > 0) {
            actor5.animationDelay--;
         }
      }
   }
   private static void advanceAudioTiming(int length) {
      for (pcmTimingAccumulator += length; pcmTimingAccumulator >= audioSampleRate; pcmBacklogMicros = pcmBacklogMicros - (pcmBacklogMicros >> 2)) {
         pcmTimingAccumulator = pcmTimingAccumulator - audioSampleRate;
      }

      if ((pcmBacklogMicros -= length * 1000) < 0) {
         pcmBacklogMicros = 0;
      }
   }
   private void ensureGameScreenBufferMatchesViewport() {
      if (screenMode == 0) {
         return;
      }

      if (this.gameScreenImageProducer == null
         || this.gameScreenImageProducer.getWidth() != clientWidth
         || this.gameScreenImageProducer.getHeight() != clientHeight) {
         this.rebuildViewportBuffers();
      }
   }

   private void drawGameScreen() {
      this.ensureGameScreenBufferMatchesViewport();
      if (this.fullscreenInterfaceId == -1 || this.loadingStage != 2 && super.graphicsBuffer == null) {
         if (this.interfaceRedrawCounter != 0) {
            this.setupGameScreenBuffers();
         }

         if (this.welcomeScreenRaised) {
            this.welcomeScreenRaised = false;
            if (screenMode == 0) {
               this.backVmidIP2_2.drawToBuffer(4, this.frameBuffer, 0);
            }

            this.needDrawTabArea = true;
            this.inputTaken = true;
            this.tabAreaAltered = true;
            this.chatSettingsRedraw = true;
            if (this.loadingStage != 2) {
               this.gameScreenImageProducer.drawToBuffer(screenMode == 0 ? 4 : 0, this.frameBuffer, screenMode == 0 ? 4 : 0);
               if (screenMode == 0) {
                  if (gameframeVersion == 474 && orbsEnabled) {
                     this.minimapImageProducer.drawToBuffer(4, this.frameBuffer, 516);
                  } else if (gameframeVersion == 474 && !orbsEnabled) {
                     this.minimapImageProducer.drawToBuffer(4, this.frameBuffer, 545);
                  } else {
                     this.minimapImageProducer.drawToBuffer(4, this.frameBuffer, 550);
                  }
               }
            }
         }

         if (this.menuOpen && this.menuScreenArea == 1) {
            this.needDrawTabArea = true;
         }

         if (this.invOverlayInterfaceID != -1 && this.animateInterface(this.animationCycleDelta, this.invOverlayInterfaceID)) {
            this.needDrawTabArea = true;
         }

         if (this.atInventoryInterfaceType == 2) {
            this.needDrawTabArea = true;
         }

         if (this.activeInterfaceType == 2) {
            this.needDrawTabArea = true;
         }

         if (this.needDrawTabArea) {
            if (screenMode == 0) {
               this.drawTabArea();
            }

            this.needDrawTabArea = false;
         }

         if (this.backDialogID == -1 && this.inputDialogState == 3) {
            if (gameframeVersion != 474) {
               int localClanChatMode = this.clanChatMode * 14;
               this.autocastWidget.scrollPosition = this.autoCastId;
               if (super.mouseX > 448 && super.mouseX < 560 && super.mouseY > 332) {
                  this.handleScrollbar(463, 77, super.mouseX - 17, super.mouseY - 357, this.autocastWidget, 0, false, localClanChatMode);
               }

               int autoCastIdOrAutocastWidget = this.autocastWidget.scrollPosition;
               if (this.autocastWidget.scrollPosition < 0) {
                  autoCastIdOrAutocastWidget = 0;
               }

               if (autoCastIdOrAutocastWidget > localClanChatMode - 77) {
                  autoCastIdOrAutocastWidget = localClanChatMode - 77;
               }

               if (this.autoCastId != autoCastIdOrAutocastWidget) {
                  this.autoCastId = autoCastIdOrAutocastWidget;
                  this.inputTaken = true;
               }
            } else {
               int clanChatMode2 = this.clanChatMode * 14;
               this.autocastWidget.scrollPosition = this.autoCastId;
               if (super.mouseX > 478 && super.mouseX < 580 && super.mouseY > (screenMode == 0 ? 342 : clientHeight - 161)) {
                  this.handleScrollbar(494, 110, super.mouseX, super.mouseY - (screenMode == 0 ? 345 : clientHeight - 155), this.autocastWidget, 0, false, clanChatMode2);
               }

               int scrollPosition = this.autocastWidget.scrollPosition;
               if (this.autocastWidget.scrollPosition < 0) {
                  scrollPosition = 0;
               }

               if (scrollPosition > clanChatMode2 - 110) {
                  scrollPosition = clanChatMode2 - 110;
               }

               if (this.autoCastId != scrollPosition) {
                  this.autoCastId = scrollPosition;
                  this.inputTaken = true;
               }
            }
         }

         if (this.backDialogID == -1 && this.inputDialogState != 3) {
            if (gameframeVersion != 474) {
               this.autocastWidget.scrollPosition = this.chatContentHeight - this.chatScrollOffset - 77;
               if (super.mouseX > 448 && super.mouseX < 560 && super.mouseY > 332) {
                  this.handleScrollbar(463, 77, super.mouseX - 17, super.mouseY - 357, this.autocastWidget, 0, false, this.chatContentHeight);
               }

               int chatScrollOffsetOrChatContentHeight;
               if ((chatScrollOffsetOrChatContentHeight = this.chatContentHeight - 77 - this.autocastWidget.scrollPosition) < 0) {
                  chatScrollOffsetOrChatContentHeight = 0;
               }

               if (chatScrollOffsetOrChatContentHeight > this.chatContentHeight - 77) {
                  chatScrollOffsetOrChatContentHeight = this.chatContentHeight - 77;
               }

               if (this.chatScrollOffset != chatScrollOffsetOrChatContentHeight) {
                  this.chatScrollOffset = chatScrollOffsetOrChatContentHeight;
                  this.inputTaken = true;
               }
            } else {
               this.autocastWidget.scrollPosition = this.chatContentHeight - this.chatScrollOffset - 110;
               if (super.mouseX > 478 && super.mouseX < 580 && super.mouseY > (screenMode == 0 ? 342 : clientHeight - 161)) {
                  this.handleScrollbar(
                     494, 110, super.mouseX, super.mouseY - (screenMode == 0 ? 345 : clientHeight - 155), this.autocastWidget, 0, false, this.chatContentHeight
                  );
               }

               int sourceChatScrollOffset;
               if ((sourceChatScrollOffset = this.chatContentHeight - 110 - this.autocastWidget.scrollPosition) < 0) {
                  sourceChatScrollOffset = 0;
               }

               if (sourceChatScrollOffset > this.chatContentHeight - 110) {
                  sourceChatScrollOffset = this.chatContentHeight - 110;
               }

               if (this.chatScrollOffset != sourceChatScrollOffset) {
                  this.chatScrollOffset = sourceChatScrollOffset;
                  this.inputTaken = true;
               }
            }
         }

         if (this.backDialogID != -1 && this.animateInterface(this.animationCycleDelta, this.backDialogID)) {
            this.inputTaken = true;
         }

         if (this.atInventoryInterfaceType == 3) {
            this.inputTaken = true;
         }

         if (this.activeInterfaceType == 3) {
            this.inputTaken = true;
         }

         if (this.inputDialogState == 3 && gameframeVersion != 474) {
            this.inputTaken = true;
         }

         if (this.clickToContinueString != null) {
            this.inputTaken = true;
         }

         if (this.menuOpen && this.menuScreenArea == 2) {
            this.inputTaken = true;
         }

         if (this.inputTaken) {
            if (screenMode == 0) {
               this.drawChatArea();
            }

            this.inputTaken = false;
            if (gameframeVersion == 474 && screenMode == 0) {
               this.bottomFrameStripBuffer.initDrawingArea();
               this.drawChannelButtons();
               this.bottomFrameStripBuffer.drawToBuffer(476, this.frameBuffer, 0);
               this.gameScreenImageProducer.initDrawingArea();
            }
         }

         if (this.loadingStage == 2) {
            this.gameScreenImageProducer.initDrawingArea();
            Client client = this;
            this.sceneCycle++;
            client.addLocalAndTargetPlayerToScene();
            client.showNPCs(true);
            client.addOtherPlayersToScene();
            client.showNPCs(false);
            client.updateProjectiles();
            client.createStationaryGraphics();
            if (!client.oriented) {
               int cameraPitch = client.cameraPitch;
               if (client.cameraDistanceScale / 256 > cameraPitch) {
                  cameraPitch = client.cameraDistanceScale / 256;
               }

               if (client.cameraShakeActive[4] && client.cameraShakeSineAmplitude[4] + 128 > cameraPitch) {
                  cameraPitch = client.cameraShakeSineAmplitude[4] + 128;
               }

               int scalar = client.minimapInt1 + client.cameraRotation & 2047;
               client.setCameraPos(
                  cameraZoom + cameraPitch * 3, cameraPitch, client.cameraFocusX, client.getTileHeight(client.plane, localPlayer.worldY, localPlayer.worldX) - 50, scalar, client.cameraFocusY
               );
            }

            int localGetCameraPlane;
            if (!client.oriented) {
               localGetCameraPlane = client.getCameraPlane();
            } else {
               localGetCameraPlane = client.getRoofPlane();
            }

            int cameraPositionX = client.cameraPositionX;
            int cameraPositionZ = client.cameraPositionZ;
            int xCameraPos = client.xCameraPos;
            int zCameraPos = client.zCameraPos;
            int yCameraPos = client.yCameraPos;

            for (int cameraShakeActiveIndex = 0; cameraShakeActiveIndex < 5; cameraShakeActiveIndex++) {
               if (client.cameraShakeActive[cameraShakeActiveIndex]) {
                  int scalar2 = (int)(
                     Math.random() * ((client.cameraShakeRandomAmplitude[cameraShakeActiveIndex] << 1) + 1)
                        - client.cameraShakeRandomAmplitude[cameraShakeActiveIndex]
                        + Math.sin((double)client.cameraShakePhase[cameraShakeActiveIndex] * client.cameraShakeSineFrequency[cameraShakeActiveIndex] / 100.0) * client.cameraShakeSineAmplitude[cameraShakeActiveIndex]
                  );
                  if (cameraShakeActiveIndex == 0) {
                     client.cameraPositionX += scalar2;
                  }

                  if (cameraShakeActiveIndex == 1) {
                     client.cameraPositionZ += scalar2;
                  }

                  if (cameraShakeActiveIndex == 2) {
                     client.xCameraPos += scalar2;
                  }

                  if (cameraShakeActiveIndex == 3) {
                     client.yCameraPos = client.yCameraPos + scalar2 & 2047;
                  }

                  if (cameraShakeActiveIndex == 4) {
                     client.zCameraPos += scalar2;
                     if (client.zCameraPos < 128) {
                        client.zCameraPos = 128;
                     }

                     if (client.zCameraPos > 383) {
                        client.zCameraPos = 383;
                     }
                  }
               }
            }

            int textureUsageCounter = Rasterizer3D.textureUsageCounter;
            Model.pickingEnabled = true;
            Model.pickedCount = 0;
            Model.mouseX = client.mouseX - 4;
            Model.mouseY = client.mouseY - 4;
            Rasterizer2D.clear();
            client.scene.renderScene(client.cameraPositionX, client.xCameraPos, client.yCameraPos, client.cameraPositionZ, localGetCameraPlane, client.zCameraPos);
            client.scene.clearInteractiveObjectCache();
            client.updateFog();
            client.updateParticles();
            client.drawEntityOverlays();
            client.drawHeadIcon();
            client.animateTextures(textureUsageCounter);
            client.draw3dScreen();
            if (xpDropPosition != 0) {
               client.drawExperienceDrops();
            }

            client.gameScreenImageProducer.drawToBuffer(screenMode == 0 ? 4 : 0, client.frameBuffer, screenMode == 0 ? 4 : 0);
            client.cameraPositionX = cameraPositionX;
            client.cameraPositionZ = cameraPositionZ;
            client.xCameraPos = xCameraPos;
            client.zCameraPos = zCameraPos;
            client.yCameraPos = yCameraPos;
         }

         if (this.loadingStage == 2) {
            this.drawMinimap();
            if (screenMode == 0) {
               if (gameframeVersion == 474 && orbsEnabled) {
                  this.minimapImageProducer.drawToBuffer(4, this.frameBuffer, 516);
               } else if (gameframeVersion == 474 && !orbsEnabled) {
                  this.minimapImageProducer.drawToBuffer(4, this.frameBuffer, 545);
               } else {
                  this.minimapImageProducer.drawToBuffer(4, this.frameBuffer, 550);
               }
            }
         }

         if (this.flashingSidebarId != -1) {
            this.tabAreaAltered = true;
         }

         if (this.tabAreaAltered) {
            if (this.flashingSidebarId != -1 && this.flashingSidebarId == this.currentTab) {
               this.flashingSidebarId = -1;
               this.outgoingBuffer.writeOpcode(120);
               this.outgoingBuffer.writeByte(this.currentTab);
            }

            this.tabAreaAltered = false;
            if (gameframeVersion == 474 && this.loadedGameframeVersion != 474) {
               this.loadedGameframeVersion = 474;
               this.minimapLeftFrameStripBuffer = null;
               this.getGameComponent();
               this.minimapLeftFrameStripBuffer = new BufferedImageGraphicsBuffer(29, 156);
               this.chatboxImageProducer = null;
               int sourceModernChatRasterWidth = modernChatRasterWidth;
               int sourceModernChatRasterHeight = modernChatRasterHeight;
               this.getGameComponent();
               this.chatboxImageProducer = new BufferedImageGraphicsBuffer(sourceModernChatRasterWidth, sourceModernChatRasterHeight);
               int modernChatRasterWidth2 = modernChatRasterWidth;
               int modernChatRasterHeight2 = modernChatRasterHeight;
               this.getGameComponent();
               new BufferedImageGraphicsBuffer(modernChatRasterWidth2, modernChatRasterHeight2);
               this.bottomFrameStripBuffer = null;
               this.getGameComponent();
               this.bottomFrameStripBuffer = new BufferedImageGraphicsBuffer(520, 27);
               this.chatLeftFrameStripBuffer = null;
               this.getGameComponent();
               this.chatLeftFrameStripBuffer = new BufferedImageGraphicsBuffer(7, 131);
               this.chatRightFrameStripBuffer = null;
               this.getGameComponent();
               this.chatRightFrameStripBuffer = new BufferedImageGraphicsBuffer(34, 121);
               this.chatTopFrameStripBuffer = null;
               this.getGameComponent();
               this.chatTopFrameStripBuffer = new BufferedImageGraphicsBuffer(547, 7);
               this.middleRightFrameStripBuffer = null;
               this.getGameComponent();
               this.middleRightFrameStripBuffer = new BufferedImageGraphicsBuffer(31, 133);
               this.rightFrameStripBuffer = null;
               this.getGameComponent();
               this.rightFrameStripBuffer = new BufferedImageGraphicsBuffer(28, 261);
               this.tabImageProducer = null;
               sourceModernChatRasterWidth = modernSidebarRasterWidth;
               this.getGameComponent();
               this.tabImageProducer = new BufferedImageGraphicsBuffer(sourceModernChatRasterWidth, 261);
               this.drawTabArea();
               this.drawChatArea();
               if (screenMode == 0) {
                  this.minimapImageProducer.initDrawingArea();
                  if (gameframeVersion == 474 && orbsEnabled) {
                     customSprites[25].drawSprite(0, 0);
                     mapBack.drawBackground(29, 0);
                     this.minimapImageProducer.drawToBuffer(4, this.frameBuffer, 516);
                  } else if (gameframeVersion == 474 && !orbsEnabled) {
                     mapBack.drawBackground(0, 0);
                     this.minimapImageProducer.drawToBuffer(4, this.frameBuffer, 545);
                  } else {
                     mapBack.drawBackground(0, 0);
                     this.minimapImageProducer.drawToBuffer(4, this.frameBuffer, 550);
                  }
               }
            }

            if (gameframeVersion != 474 && this.loadedGameframeVersion == 474) {
               this.loadedGameframeVersion = 317;
               this.minimapLeftFrameStripBuffer = null;
               this.getGameComponent();
               this.minimapLeftFrameStripBuffer = new BufferedImageGraphicsBuffer(34, 156);
               this.chatboxImageProducer = null;
               int sourceClassicChatRasterWidth = classicChatRasterWidth;
               int sourceClassicChatRasterHeight = classicChatRasterHeight;
               this.getGameComponent();
               this.chatboxImageProducer = new BufferedImageGraphicsBuffer(sourceClassicChatRasterWidth, sourceClassicChatRasterHeight);
               this.bottomFrameStripBuffer = null;
               this.getGameComponent();
               this.bottomFrameStripBuffer = new BufferedImageGraphicsBuffer(496, 50);
               this.chatLeftFrameStripBuffer = null;
               this.getGameComponent();
               this.chatLeftFrameStripBuffer = new BufferedImageGraphicsBuffer(17, 96);
               this.chatRightFrameStripBuffer = null;
               this.getGameComponent();
               this.chatRightFrameStripBuffer = new BufferedImageGraphicsBuffer(57, 109);
               this.chatTopFrameStripBuffer = null;
               this.getGameComponent();
               this.chatTopFrameStripBuffer = new BufferedImageGraphicsBuffer(553, 19);
               this.middleRightFrameStripBuffer = null;
               this.getGameComponent();
               this.middleRightFrameStripBuffer = new BufferedImageGraphicsBuffer(37, 133);
               this.rightFrameStripBuffer = null;
               this.getGameComponent();
               this.rightFrameStripBuffer = new BufferedImageGraphicsBuffer(22, 261);
               this.tabImageProducer = null;
               sourceClassicChatRasterWidth = musicVolume;
               this.getGameComponent();
               this.tabImageProducer = new BufferedImageGraphicsBuffer(sourceClassicChatRasterWidth, 261);
               this.drawTabArea();
               this.drawChatArea();
               if (screenMode == 0) {
                  this.minimapImageProducer.initDrawingArea();
                  if (gameframeVersion == 474 && orbsEnabled) {
                     customSprites[25].drawSprite(0, 0);
                     mapBack.drawBackground(29, 0);
                     this.minimapImageProducer.drawToBuffer(4, this.frameBuffer, 516);
                  } else if (gameframeVersion == 474 && !orbsEnabled) {
                     mapBack.drawBackground(0, 0);
                     this.minimapImageProducer.drawToBuffer(4, this.frameBuffer, 545);
                  } else {
                     mapBack.drawBackground(0, 0);
                     this.minimapImageProducer.drawToBuffer(4, this.frameBuffer, 550);
                  }
               }
            }

            if (screenMode == 0) {
               this.rightFrameStripBuffer.initDrawingArea();
               if (gameframeVersion != 474) {
                  backRight2Sprite.drawSprite(0, 0);
                  this.rightFrameStripBuffer.drawToBuffer(205, this.frameBuffer, 743);
               } else {
                  customSprites[20].drawSprite(0, 0);
                  this.rightFrameStripBuffer.drawToBuffer(205, this.frameBuffer, 737);
               }

               this.middleRightFrameStripBuffer.initDrawingArea();
               if (gameframeVersion != 474) {
                  backVmid2Sprite.drawSprite(0, 0);
               } else {
                  customSprites[22].drawSprite(0, 0);
               }

               this.middleRightFrameStripBuffer.drawToBuffer(205, this.frameBuffer, 516);
               this.chatRightFrameStripBuffer.initDrawingArea();
               if (gameframeVersion != 474) {
                  backVmid3Sprite.drawSprite(0, 0);
                  this.chatRightFrameStripBuffer.drawToBuffer(357, this.frameBuffer, 496);
               } else {
                  customSprites[17].drawSprite(0, 0);
                  this.chatRightFrameStripBuffer.drawToBuffer(345, this.frameBuffer, 513);
               }

               this.chatTopFrameStripBuffer.initDrawingArea();
               if (gameframeVersion != 474) {
                  backHmid2Sprite.drawSprite(0, 0);
               } else {
                  customSprites[18].drawSprite(0, 0);
               }

               this.chatTopFrameStripBuffer.drawToBuffer(338, this.frameBuffer, 0);
               if (screenMode == 0) {
                  this.chatLeftFrameStripBuffer.initDrawingArea();
                  if (gameframeVersion != 474) {
                     backLeft2Sprite.drawSprite(0, 0);
                     this.chatLeftFrameStripBuffer.drawToBuffer(357, this.frameBuffer, 0);
                  } else {
                     customSprites[16].drawSprite(0, 0);
                     this.chatLeftFrameStripBuffer.drawToBuffer(345, this.frameBuffer, 0);
                  }
               }

               this.minimapRightFrameStripBuffer.initDrawingArea();
               if (gameframeVersion != 474) {
                  backRight1Sprite.drawSprite(0, 0);
               } else {
                  customSprites[24].drawSprite(0, 0);
               }

               this.minimapRightFrameStripBuffer.drawToBuffer(4, this.frameBuffer, gameframeVersion == 474 ? 717 : 722);
               if (gameframeVersion != 474) {
                  this.minimapLeftFrameStripBuffer.initDrawingArea();
                  backVmid1Sprite.drawSprite(0, 0);
                  this.minimapLeftFrameStripBuffer.drawToBuffer(4, this.frameBuffer, 516);
               } else if (gameframeVersion == 474 && !orbsEnabled) {
                  this.minimapLeftFrameStripBuffer.initDrawingArea();
                  customSprites[25].drawSprite(0, 0);
                  this.minimapLeftFrameStripBuffer.drawToBuffer(4, this.frameBuffer, 516);
               }

               if (screenMode == 0) {
                  this.chatSettingImageProducer.initDrawingArea();
                  if (gameframeVersion != 474) {
                     backTop1Sprite.drawSprite(0, 0);
                  } else {
                     customSprites[26].drawSprite(0, 0);
                  }

                  this.chatSettingImageProducer.drawToBuffer(0, this.frameBuffer, 0);
               }

               if (screenMode == 0) {
                  this.topRightFrameStripBuffer.initDrawingArea();
                  if (gameframeVersion != 474) {
                     this.backHmid1.drawBackground(0, 0);
                  } else {
                     customSprites[21].drawSprite(0, 0);
                  }
               }

               if (this.invOverlayInterfaceID == -1) {
                  if (this.tabInterfaceIds[this.currentTab] != -1) {
                     if (gameframeVersion != 474) {
                        if (this.currentTab == 0) {
                           this.redStone1.drawBackground(22, 10);
                        }

                        if (this.currentTab == 1) {
                           this.redStone2.drawBackground(54, 8);
                        }

                        if (this.currentTab == 2) {
                           this.redStone2.drawBackground(82, 8);
                        }

                        if (this.currentTab == 3) {
                           this.redStone3.drawBackground(110, 8);
                        }

                        if (this.currentTab == 4) {
                           this.redStone2_2.drawBackground(153, 8);
                        }

                        if (this.currentTab == 5) {
                           this.redStone2_2.drawBackground(181, 8);
                        }

                        if (this.currentTab == 6) {
                           this.redStone1_2.drawBackground(209, 9);
                        }
                     } else {
                        if (this.currentTab == 0) {
                           customSprites[37].drawSprite(6, 8);
                        }

                        if (this.currentTab == 1) {
                           customSprites[41].drawSprite(44, 8);
                        }

                        if (this.currentTab == 2) {
                           customSprites[41].drawSprite(77, 8);
                        }

                        if (this.currentTab == 3) {
                           customSprites[41].drawSprite(110, 8);
                        }

                        if (this.currentTab == 4) {
                           customSprites[41].drawSprite(143, 8);
                        }

                        if (this.currentTab == 5) {
                           customSprites[41].drawSprite(176, 8);
                        }

                        if (this.currentTab == 6) {
                           customSprites[38].drawSprite(209, 8);
                        }
                     }
                  }

                  if (gameframeVersion == 317) {
                     if (this.tabInterfaceIds[0] != -1 && (this.flashingSidebarId != 0 || gameCycle % 20 < 10)) {
                        this.sideIcons[0].drawBackground(29, 13);
                     }

                     if (this.tabInterfaceIds[1] != -1 && (this.flashingSidebarId != 1 || gameCycle % 20 < 10)) {
                        this.sideIcons[1].drawBackground(53, 11);
                     }

                     if (this.tabInterfaceIds[2] != -1 && (this.flashingSidebarId != 2 || gameCycle % 20 < 10)) {
                        this.sideIcons[2].drawBackground(82, 11);
                     }

                     if (this.tabInterfaceIds[3] != -1 && (this.flashingSidebarId != 3 || gameCycle % 20 < 10)) {
                        this.sideIcons[3].drawBackground(115, 12);
                     }

                     if (this.tabInterfaceIds[4] != -1 && (this.flashingSidebarId != 4 || gameCycle % 20 < 10)) {
                        this.sideIcons[4].drawBackground(153, 13);
                     }

                     if (this.tabInterfaceIds[5] != -1 && (this.flashingSidebarId != 5 || gameCycle % 20 < 10)) {
                        this.sideIcons[5].drawBackground(180, 11);
                     }

                     if (this.tabInterfaceIds[6] != -1 && (this.flashingSidebarId != 6 || gameCycle % 20 < 10)) {
                        this.sideIcons[6].drawBackground(208, 13);
                     }
                  } else if (gameframeVersion == 459 || gameframeVersion == 474) {
                     if (this.tabInterfaceIds[0] != -1 && (this.flashingSidebarId != 0 || gameCycle % 20 < 10)) {
                        byte byteCode = 0;
                        byte byteCode2 = 0;
                        if (gameframeVersion == 474) {
                           byteCode = -17;
                           byteCode2 = -5;
                        }

                        customSprites[1].drawSprite(byteCode + 33, byteCode2 + 21);
                     }

                     if (this.tabInterfaceIds[1] != -1 && (this.flashingSidebarId != 1 || gameCycle % 20 < 10)) {
                        byte byteCode3 = 0;
                        byte byteCode4 = 0;
                        if (gameframeVersion == 474) {
                           byteCode3 = -10;
                           byteCode4 = -4;
                        }

                        customSprites[2].drawSprite(byteCode3 + 58, byteCode4 + 18);
                     }

                     if (this.tabInterfaceIds[2] != -1 && (this.flashingSidebarId != 2 || gameCycle % 20 < 10)) {
                        byte byteCode5 = 0;
                        byte byteCode6 = 0;
                        if (gameframeVersion == 474) {
                           byteCode5 = -3;
                           byteCode6 = -2;
                        }

                        customSprites[3].drawSprite(byteCode5 + 86, byteCode6 + 17);
                     }

                     if (this.tabInterfaceIds[3] != -1 && (this.flashingSidebarId != 3 || gameCycle % 20 < 10)) {
                        byte byteCode7 = 0;
                        byte customSpriteIndex = 4;
                        if (gameframeVersion == 474) {
                           byteCode7 = -2;
                           customSpriteIndex = 42;
                        }

                        customSprites[customSpriteIndex].drawSprite(byteCode7 + 116, 13);
                     }

                     if (this.tabInterfaceIds[4] != -1 && (this.flashingSidebarId != 4 || gameCycle % 20 < 10)) {
                        byte byteCode8 = 0;
                        byte byteCode9 = 0;
                        if (gameframeVersion == 474) {
                           byteCode8 = -10;
                           byteCode9 = -3;
                        }

                        customSprites[5].drawSprite(byteCode8 + 156, byteCode9 + 13);
                     }

                     if (this.tabInterfaceIds[5] != -1 && (this.flashingSidebarId != 5 || gameCycle % 20 < 10)) {
                        byte byteCode10 = 0;
                        byte byteCode11 = 0;
                        if (gameframeVersion == 474) {
                           byteCode10 = -5;
                           byteCode11 = -3;
                        }

                        customSprites[6].drawSprite(byteCode10 + 184, byteCode11 + 14);
                     }

                     if (this.tabInterfaceIds[6] != -1 && (this.flashingSidebarId != 6 || gameCycle % 20 < 10)) {
                        byte byteCode12 = 0;
                        byte byteCode13 = 0;
                        if (gameframeVersion == 474) {
                           byteCode12 = 2;
                           byteCode13 = -4;
                        }

                        customSprites[7].drawSprite(byteCode12 + 212, byteCode13 + 19);
                     }
                  }
               }

               this.topRightFrameStripBuffer.drawToBuffer(160, this.frameBuffer, 516);
            }

            if (screenMode == 0) {
               this.bottomRightFrameStripBuffer.initDrawingArea();
               if (gameframeVersion != 474) {
                  this.backBase2.drawBackground(0, 0);
               } else {
                  customSprites[19].drawSprite(0, 0);
               }

               if (this.invOverlayInterfaceID == -1) {
                  if (this.tabInterfaceIds[this.currentTab] != -1) {
                     if (gameframeVersion != 474) {
                        if (this.currentTab == 7) {
                           this.redStone1_3.drawBackground(42, 0);
                        }

                        if (this.currentTab == 8) {
                           this.redStone2_3.drawBackground(74, 0);
                        }

                        if (this.currentTab == 9) {
                           this.redStone2_3.drawBackground(102, 0);
                        }

                        if (this.currentTab == 10) {
                           this.redStone3_2.drawBackground(130, 1);
                        }

                        if (this.currentTab == 11) {
                           this.redStone2_4.drawBackground(173, 0);
                        }

                        if (this.currentTab == 12) {
                           this.redStone2_4.drawBackground(201, 0);
                        }

                        if (this.currentTab == 13) {
                           this.redStone1_4.drawBackground(229, 0);
                        }
                     } else {
                        if (this.currentTab == 7) {
                           customSprites[39].drawSprite(26, 0);
                        }

                        if (this.currentTab == 8) {
                           customSprites[41].drawSprite(64, 0);
                        }

                        if (this.currentTab == 9) {
                           customSprites[41].drawSprite(97, 0);
                        }

                        if (this.currentTab == 10) {
                           customSprites[41].drawSprite(130, 0);
                        }

                        if (this.currentTab == 11) {
                           customSprites[41].drawSprite(163, 0);
                        }

                        if (this.currentTab == 12) {
                           customSprites[41].drawSprite(196, 0);
                        }

                        if (this.currentTab == 13) {
                           customSprites[40].drawSprite(229, 0);
                        }
                     }
                  }

                  if (gameframeVersion == 317) {
                     if (this.tabInterfaceIds[8] != -1 && (this.flashingSidebarId != 8 || gameCycle % 20 < 10)) {
                        this.sideIcons[7].drawBackground(74, 2);
                     }

                     if (this.tabInterfaceIds[9] != -1 && (this.flashingSidebarId != 9 || gameCycle % 20 < 10)) {
                        this.sideIcons[8].drawBackground(102, 3);
                     }

                     if (this.tabInterfaceIds[10] != -1 && (this.flashingSidebarId != 10 || gameCycle % 20 < 10)) {
                        this.sideIcons[9].drawBackground(137, 4);
                     }

                     if (this.tabInterfaceIds[11] != -1 && (this.flashingSidebarId != 11 || gameCycle % 20 < 10)) {
                        this.sideIcons[10].drawBackground(174, 2);
                     }

                     if (this.tabInterfaceIds[12] != -1 && (this.flashingSidebarId != 12 || gameCycle % 20 < 10)) {
                        this.sideIcons[11].drawBackground(201, 2);
                     }

                     if (this.tabInterfaceIds[13] != -1 && (this.flashingSidebarId != 13 || gameCycle % 20 < 10)) {
                        this.sideIcons[12].drawBackground(226, 2);
                     }
                  } else if (gameframeVersion == 459 || gameframeVersion == 474) {
                     if (this.tabInterfaceIds[8] != -1 && (this.flashingSidebarId != 8 || gameCycle % 20 < 10)) {
                        byte byteCode14 = 0;
                        if (gameframeVersion == 474) {
                           byteCode14 = -9;
                        }

                        customSprites[8].drawSprite(byteCode14 + 78, 8);
                     }

                     if (this.tabInterfaceIds[9] != -1 && (this.flashingSidebarId != 9 || gameCycle % 20 < 10)) {
                        byte byteCode15 = 0;
                        byte byteCode16 = 0;
                        if (gameframeVersion == 474) {
                           byteCode15 = -4;
                           byteCode16 = -1;
                        }

                        customSprites[9].drawSprite(byteCode15 + 106, byteCode16 + 9);
                     }

                     if (this.tabInterfaceIds[10] != -1 && (this.flashingSidebarId != 10 || gameCycle % 20 < 10)) {
                        byte byteCode17 = 0;
                        if (gameframeVersion == 474) {
                           byteCode17 = -6;
                        }

                        customSprites[10].drawSprite(byteCode17 + 142, 4);
                     }

                     if (this.tabInterfaceIds[11] != -1 && (this.flashingSidebarId != 11 || gameCycle % 20 < 10)) {
                        byte byteCode18 = 0;
                        byte byteCode19 = 0;
                        if (gameframeVersion == 474) {
                           byteCode18 = -9;
                           byteCode19 = -1;
                        }

                        customSprites[11].drawSprite(byteCode18 + 177, byteCode19 + 7);
                     }

                     if (this.tabInterfaceIds[12] != -1 && (this.flashingSidebarId != 12 || gameCycle % 20 < 10)) {
                        byte byteCode20 = 0;
                        byte byteCode21 = 0;
                        if (gameframeVersion == 474) {
                           byteCode20 = -3;
                           byteCode21 = 1;
                        }

                        customSprites[12].drawSprite(byteCode20 + 207, byteCode21 + 3);
                     }

                     if (this.tabInterfaceIds[13] != -1 && (this.flashingSidebarId != 13 || gameCycle % 20 < 10)) {
                        byte byteCode22 = 0;
                        byte byteCode23 = 0;
                        if (gameframeVersion == 474) {
                           byteCode22 = 5;
                           byteCode23 = 1;
                        }

                        customSprites[13].drawSprite(byteCode22 + 231, byteCode23 + 4);
                     }
                  }
               }

               this.bottomRightFrameStripBuffer.drawToBuffer(466, this.frameBuffer, 496);
            }

            this.gameScreenImageProducer.initDrawingArea();
         }

         if (this.chatSettingsRedraw) {
            this.chatSettingsRedraw = false;
            if (screenMode == 0) {
               this.bottomFrameStripBuffer.initDrawingArea();
               if (gameframeVersion != 474) {
                  this.backBase1.drawBackground(0, 0);
                  this.plainFont.textCenterShadow(16777215, 55, "Public chat", 28, true);
                  if (this.publicChatMode == 0) {
                     this.plainFont.textCenterShadow(65280, 55, "On", 41, true);
                  }

                  if (this.publicChatMode == 1) {
                     this.plainFont.textCenterShadow(16776960, 55, "Friends", 41, true);
                  }

                  if (this.publicChatMode == 2) {
                     this.plainFont.textCenterShadow(16711680, 55, "Off", 41, true);
                  }

                  if (this.publicChatMode == 3) {
                     this.plainFont.textCenterShadow(65535, 55, "Hide", 41, true);
                  }

                  this.plainFont.textCenterShadow(16777215, 184, "Private chat", 28, true);
                  if (this.privateChatMode == 0) {
                     this.plainFont.textCenterShadow(65280, 184, "On", 41, true);
                  }

                  if (this.privateChatMode == 1) {
                     this.plainFont.textCenterShadow(16776960, 184, "Friends", 41, true);
                  }

                  if (this.privateChatMode == 2) {
                     this.plainFont.textCenterShadow(16711680, 184, "Off", 41, true);
                  }

                  this.plainFont.textCenterShadow(16777215, 324, "Trade/compete", 28, true);
                  if (this.tradeMode == 0) {
                     this.plainFont.textCenterShadow(65280, 324, "On", 41, true);
                  }

                  if (this.tradeMode == 1) {
                     this.plainFont.textCenterShadow(16776960, 324, "Friends", 41, true);
                  }

                  if (this.tradeMode == 2) {
                     this.plainFont.textCenterShadow(16711680, 324, "Off", 41, true);
                  }

                  this.plainFont.textCenterShadow(16777215, 458, "Report abuse", 33, true);
               } else {
                  this.drawChannelButtons();
               }

               if (gameframeVersion != 474) {
                  this.bottomFrameStripBuffer.drawToBuffer(453, this.frameBuffer, 0);
               } else {
                  this.bottomFrameStripBuffer.drawToBuffer(476, this.frameBuffer, 0);
               }
            }

            this.gameScreenImageProducer.initDrawingArea();
         }

         this.animationCycleDelta = 0;
         if (autoScreenshots) {
            if ((this.openInterfaceId == 12140 || this.openInterfaceId == 6960 || this.openInterfaceId == 19550 || this.openInterfaceId == 6733) && this.lastScreenshotInterfaceId != this.openInterfaceId) {
               this.autoScreenshotDelay++;
               if (this.autoScreenshotDelay >= 10) {
                  ClientWindow.saveFramebufferScreenshot();
                  this.lastScreenshotInterfaceId = this.openInterfaceId;
               }
            }

            if (this.openInterfaceId == -1 && this.lastScreenshotInterfaceId != -1) {
               this.lastScreenshotInterfaceId = -1;
               this.autoScreenshotDelay = 0;
            }

            if ((
                  this.backDialogID == 6247
                     || this.backDialogID == 6253
                     || this.backDialogID == 6206
                     || this.backDialogID == 6216
                     || this.backDialogID == 4443
                     || this.backDialogID == 6242
                     || this.backDialogID == 6211
                     || this.backDialogID == 6226
                     || this.backDialogID == 4272
                     || this.backDialogID == 6231
                     || this.backDialogID == 6258
                     || this.backDialogID == 4282
                     || this.backDialogID == 6263
                     || this.backDialogID == 6221
                     || this.backDialogID == 4416
                     || this.backDialogID == 6237
                     || this.backDialogID == 4277
                     || this.backDialogID == 4261
                     || this.backDialogID == 12122
                     || this.backDialogID == 310 && Widget.widgets[311].defaultMediaId == 5340
                     || this.backDialogID == 4267
               )
               && this.lastScreenshotDialogId != this.backDialogID) {
               this.autoScreenshotDelay++;
               if (this.autoScreenshotDelay >= 10) {
                  ClientWindow.saveFramebufferScreenshot();
                  this.lastScreenshotDialogId = this.backDialogID;
               }
            }

            if (this.backDialogID == -1 && this.lastScreenshotDialogId != -1) {
               this.lastScreenshotDialogId = -1;
               this.autoScreenshotDelay = 0;
            }

            if (this.currentStats[3] == 0 && !this.deathScreenshotTaken) {
               this.autoScreenshotDelay++;
               if (this.autoScreenshotDelay >= 10) {
                  ClientWindow.saveFramebufferScreenshot();
                  this.deathScreenshotTaken = true;
               }
            }

            if (this.currentStats[3] != 0 && this.deathScreenshotTaken) {
               this.deathScreenshotTaken = false;
               this.autoScreenshotDelay = 0;
            }
         }
      } else {
         if (this.loadingStage == 2) {
            this.animateInterface(this.animationCycleDelta, this.fullscreenInterfaceId);
            if (this.openInterfaceId != -1) {
               this.animateInterface(this.animationCycleDelta, this.openInterfaceId);
            }

            this.animationCycleDelta = 0;
            Client client2 = this;
            if (super.graphicsBuffer == null) {
               client2.chatboxImageProducer = null;
               client2.minimapImageProducer = null;
               client2.tabImageProducer = null;
               client2.gameScreenImageProducer = null;
               client2.bottomFrameStripBuffer = null;
               client2.bottomRightFrameStripBuffer = null;
               client2.topRightFrameStripBuffer = null;
               client2.rightFrameStripBuffer = null;
               client2.middleRightFrameStripBuffer = null;
               client2.chatRightFrameStripBuffer = null;
               client2.chatTopFrameStripBuffer = null;
               client2.chatLeftFrameStripBuffer = null;
               client2.minimapRightFrameStripBuffer = null;
               client2.minimapLeftFrameStripBuffer = null;
               client2.chatSettingImageProducer = null;
               client2.topLeft1BackgroundTile = null;
               client2.bottomLeft1BackgroundTile = null;
               client2.flameLeftBackground = null;
               client2.flameRightBackground = null;
               client2.bottomLeft0BackgroundTile = null;
               client2.bottomRightImageProducer = null;
               client2.loginMusicImageProducer = null;
               client2.middleLeft1BackgroundTile = null;
               client2.middleRightBackgroundBuffer = null;
               int localScreenMode = screenMode == 0 ? 765 : clientWidth;
               int screenMode2 = screenMode == 0 ? 503 : clientHeight;
               client2.getGameComponent();
               client2.graphicsBuffer = new BufferedImageGraphicsBuffer(localScreenMode, screenMode2);
               client2.welcomeScreenRaised = true;
            }

            super.graphicsBuffer.initDrawingArea();
            Rasterizer3D.scanOffsets = fullScreenScanOffsets;
            Rasterizer2D.clear();
            this.welcomeScreenRaised = true;
            if (this.openInterfaceId != -1) {
               Widget widget;
               if ((widget = Widget.widgets[this.openInterfaceId]).width == 512 && widget.height == 334 && widget.type == 0 || widget.newScroller) {
                  widget.width = screenMode == 0 ? 765 : clientWidth;
                  widget.height = screenMode == 0 ? 503 : clientHeight;
                  widget.newScroller = true;
               }

               this.drawInterface(0, screenMode == 0 ? 0 : clientWidth / 2 - 382, widget, screenMode == 0 ? 8 : clientHeight / 2 - 251);
            }

            Widget widget2;
            if ((widget2 = Widget.widgets[this.fullscreenInterfaceId]).width == 512 && widget2.height == 334 && widget2.type == 0 || widget2.newScroller) {
               widget2.width = screenMode == 0 ? 765 : clientWidth;
               widget2.height = screenMode == 0 ? 503 : clientHeight;
               widget2.newScroller = true;
            }

            this.drawInterface(0, screenMode == 0 ? 1 : clientWidth / 2 - 382, widget2, screenMode == 0 ? 0 : clientHeight / 2 - 251);
            if (!this.menuOpen) {
               this.processRightClick();
               this.drawTooltip();
            } else {
               this.drawMenu();
            }
         }

         this.interfaceRedrawCounter++;
         super.graphicsBuffer.drawToBuffer(0, this.frameBuffer, 0);
      }
   }
   private void createStationaryGraphics() {
      for (SpotAnimation spotAnimation = (SpotAnimation)this.incompleteAnimables.first(); spotAnimation != null; spotAnimation = (SpotAnimation)this.incompleteAnimables.next()) {
         if (spotAnimation.plane == this.plane && !spotAnimation.finished) {
            if (gameCycle < spotAnimation.resizeZ) {
               continue;
            }

            spotAnimation.advanceAnimation(this.animationCycleDelta);
            if (!spotAnimation.finished) {
               this.scene.addEntityWithRadius(spotAnimation.plane, 0, spotAnimation.resizeXY, -1, spotAnimation.animationId, 60, spotAnimation.modelId, spotAnimation, false);
               continue;
            }
         }

         spotAnimation.unlink();
      }
   }
   private void drawInterface(int scrollPosition, int top, Widget widget, int bottom) {
      if (widget == null) {
         System.out.println("NULL INTERFACE");
      } else {
         if (this.centeredWalkableInterface && (widget.spriteXOffset != -1 || widget.spriteYOffset != -1)) {
            int sourceTop = widget.adjustSpriteX ? clientWidth + widget.spriteXOffset : widget.spriteXOffset;
            int sourceBottom = widget.adjustSpriteY ? clientHeight + widget.spriteYOffset : widget.spriteYOffset;
            top = sourceTop;
            bottom = sourceBottom;
         }

         int viewportCenterX = Rasterizer3D.viewportCenterX;
         int viewportCenterY = Rasterizer3D.viewportCenterY;
         int[] scanOffsets = Rasterizer3D.scanOffsets;
         int[] pixels = Rasterizer2D.pixels;
         float[] depthBuffer = Rasterizer2D.depthBuffer;
         int width = Rasterizer2D.width;
         int height = Rasterizer2D.height;
         if (widget.type == 0
            && widget.childIds != null
            && (!widget.hoverOnly || this.viewportHoverWidgetId == widget.id || this.tabHoverWidgetId == widget.id || this.chatHoverWidgetId == widget.id)) {
            int topX = Rasterizer2D.topX;
            int topY = Rasterizer2D.topY;
            int bottomX = Rasterizer2D.bottomX;
            int bottomY = Rasterizer2D.bottomY;
            Rasterizer2D.setClip(bottom + widget.height, top, top + widget.width, bottom);
            int childIdsLengthOrChildIds = widget.childIds.length;

            for (int childXIndex = 0; childXIndex < childIdsLengthOrChildIds; childXIndex++) {
               int top2 = widget.childX[childXIndex] + top;
               int pixelIndex = widget.childY[childXIndex] + bottom - scrollPosition;
               if (top2 < 10000 && pixelIndex < 10000) {
                  Widget widget3;
                  if ((widget3 = Widget.widgets[widget.childIds[childXIndex]]) == null) {
                     System.out.println("NULL SUB INTERFACE");
                  } else {
                     if (this.centeredWalkableInterface && (widget3.spriteXOffset != -1 || widget3.spriteYOffset != -1)) {
                        int adjustSpriteX2 = widget3.adjustSpriteX ? clientWidth + widget3.spriteXOffset : widget3.spriteXOffset;
                        int adjustSpriteY2 = widget3.adjustSpriteY ? clientHeight + widget3.spriteYOffset : widget3.spriteYOffset;
                        top2 += adjustSpriteX2;
                        pixelIndex += adjustSpriteY2;
                     }

                     top2 += widget3.runtimeXOffset;
                     pixelIndex += widget3.runtimeYOffset;
                     if (widget3.contentType > 0) {
                        Widget widget2 = widget3;
                        Client client = this;
                        int contentType = widget2.contentType;
                        if ((widget2.contentType <= 0 || contentType > 100) && (contentType < 701 || contentType > 800)) {
                           if ((contentType < 101 || contentType > 200) && (contentType < 801 || contentType > 900)) {
                              if (contentType == 203) {
                                 int scrollMaxOrFriendCount = client.friendCount;
                                 if (client.friendServerStatus != 2) {
                                    scrollMaxOrFriendCount = 0;
                                 }

                                 widget2.scrollMax = scrollMaxOrFriendCount * 15 + 20;
                                 if (widget2.scrollMax <= widget2.height) {
                                    widget2.scrollMax = widget2.height + 1;
                                 }
                              } else if (contentType >= 401 && contentType <= 500) {
                                 contentType -= 401;
                                 if (contentType == 0 && client.friendServerStatus == 0) {
                                    widget2.message = "Loading ignore list";
                                    widget2.optionType = 0;
                                 } else if (contentType == 1 && client.friendServerStatus == 0) {
                                    widget2.message = "Please wait...";
                                    widget2.optionType = 0;
                                 } else {
                                    int ignoreCount = client.ignoreCount;
                                    if (client.friendServerStatus == 0) {
                                       ignoreCount = 0;
                                    }

                                    if (contentType >= ignoreCount) {
                                       widget2.message = "";
                                       widget2.optionType = 0;
                                    } else {
                                       widget2.message = NameUtils.formatDisplayName(NameUtils.decodeBase37(client.ignoreListAsLongs[contentType]));
                                       widget2.optionType = 1;
                                    }
                                 }
                              } else if (contentType == 503) {
                                 widget2.scrollMax = client.ignoreCount * 15 + 20;
                                 if (widget2.scrollMax <= widget2.height) {
                                    widget2.scrollMax = widget2.height + 1;
                                 }
                              } else if (contentType == 327) {
                                 widget2.modelRotation1 = 150;
                                 widget2.modelRotation2 = (int)(Math.sin(gameCycle / 40.0) * 256.0) & 2047;
                                 if (client.characterDesignNeedsRebuild) {
                                    int kitIndex = 0;

                                    while (true) {
                                       if (kitIndex >= 7) {
                                          client.characterDesignNeedsRebuild = false;
                                          Model[] values = new Model[7];
                                          int scalar = 0;

                                          for (int characterDesignKitIdIndex = 0; characterDesignKitIdIndex < 7; characterDesignKitIdIndex++) {
                                             if ((kitIndex = client.characterDesignKitIds[characterDesignKitIdIndex]) >= 0) {
                                                values[scalar++] = IdentityKit.kits[kitIndex].bodyModel();
                                             }
                                          }

                                          Model model = new Model(scalar, values);

                                          for (int characterDesignColourIndex = 0; characterDesignColourIndex < 5; characterDesignColourIndex++) {
                                             if (client.characterDesignColours[characterDesignColourIndex] != 0) {
                                                model.recolor(playerBodyColors[characterDesignColourIndex][0], playerBodyColors[characterDesignColourIndex][client.characterDesignColours[characterDesignColourIndex]]);
                                                if (characterDesignColourIndex == 1) {
                                                   model.recolor(secondaryPlayerBodyColors[0], secondaryPlayerBodyColors[client.characterDesignColours[characterDesignColourIndex]]);
                                                }
                                             }
                                          }

                                          model.skin();
                                          model.applyAnimationFrame(AnimationSequence.sequences[AnimationSequence.remapId(localPlayer.idleAnimationId)].frameIds[0]);
                                          model.light(64, 850, -30, -50, -30, true);
                                          if (localPlayer.modelMode == 1) {
                                             model.scale(Player.mode1ScaleX, Player.mode1ScaleY, Player.mode1ScaleZ);
                                          } else if (localPlayer.modelMode == 3) {
                                             model.setTriangleAlpha(Player.mode3Rotation);
                                          }

                                          widget2.defaultMediaType = 5;
                                          widget2.defaultMediaId = 0;
                                          Widget.setCachedMediaModel(false, model);
                                          break;
                                       }

                                       int characterDesignKitIds2;
                                       if ((characterDesignKitIds2 = client.characterDesignKitIds[kitIndex]) >= 0 && !IdentityKit.kits[characterDesignKitIds2].bodyLoaded()) {
                                          break;
                                       }

                                       kitIndex++;
                                    }
                                 }
                              } else if (contentType == 324) {
                                 if (client.femaleCharacterSprite == null) {
                                    client.femaleCharacterSprite = widget2.disabledSprite;
                                    client.maleCharacterSprite = widget2.enabledSprite;
                                 }

                                 if (client.maleCharacter) {
                                    widget2.disabledSprite = client.maleCharacterSprite;
                                 } else {
                                    widget2.disabledSprite = client.femaleCharacterSprite;
                                 }
                              } else if (contentType == 325) {
                                 if (client.femaleCharacterSprite == null) {
                                    client.femaleCharacterSprite = widget2.disabledSprite;
                                    client.maleCharacterSprite = widget2.enabledSprite;
                                 }

                                 if (client.maleCharacter) {
                                    widget2.disabledSprite = client.femaleCharacterSprite;
                                 } else {
                                    widget2.disabledSprite = client.maleCharacterSprite;
                                 }
                              } else if (contentType == 600) {
                                 widget2.message = client.reportAbuseInput;
                                 if (gameCycle % 20 < 10) {
                                    widget2.message = widget2.message + "|";
                                 } else {
                                    widget2.message = widget2.message + " ";
                                 }
                              } else {
                                 if (contentType == 620) {
                                    if (client.yCameraCurve <= 0 || client.yCameraCurve > 3) {
                                       widget2.message = "";
                                    } else if (client.canMute) {
                                       widget2.textColor = 16711680;
                                       widget2.message = "Moderator option: Mute player for 48 hours: <ON>";
                                    } else {
                                       widget2.textColor = 16777215;
                                       widget2.message = "Moderator option: Mute player for 48 hours: <OFF>";
                                    }
                                 }

                                 if (contentType == 660) {
                                    if (client.lastLoginIp != 0) {
                                       String text;
                                       if (client.lastLoginDaysAgo == 0) {
                                          text = "earlier today";
                                       } else if (client.lastLoginDaysAgo == 1) {
                                          text = "yesterday";
                                       } else {
                                          text = client.lastLoginDaysAgo + " days ago";
                                       }

                                       widget2.message = "You last logged in @red@" + text + "@bla@ from: @red@" + SignLink.dns;
                                    } else {
                                       widget2.message = "";
                                    }
                                 }

                                 if (contentType == 661) {
                                    if (client.daysSinceRecovChange == 30000) {
                                       widget2.message = "You have not yet set any recovery questions.\\nIt is @lre@strongly@yel@ recommended that you do so.\\n\\nIf you don't you will be @lre@unable to recover your\\n@lre@password@yel@ if you forget it, or it is stolen.";
                                    } else {
                                       String localText;
                                       if (client.daysSinceRecovChange == 0) {
                                          localText = "Earlier today";
                                       } else if (client.daysSinceRecovChange == 1) {
                                          localText = "Yesterday";
                                       } else {
                                          localText = client.daysSinceRecovChange + " days ago";
                                       }

                                       widget2.message = "\\n\\nRecovery Questions Last Set:\\n" + localText;
                                    }
                                 }

                                 if (contentType == 663) {
                                    widget2.message = String.valueOf(contentType);
                                 }

                                 if (contentType == 665) {
                                    widget2.message = "\\nYou are not a member. Choose to subscribe\\nand you'll get loads of extra benefits and\\nfeatures.";
                                    if (client.membersInt == 1) {
                                       widget2.message = "\\nYou have " + client.daysSinceLastLogin + " days of\\nmembership left.";
                                    }
                                 }

                                 if (contentType == 662) {
                                    String text2 = "";
                                    if (client.unreadMessages == 0) {
                                       text2 = "@yel@0 unread messages";
                                    }

                                    if (client.unreadMessages == 1) {
                                       text2 = "@gre@1 unread message";
                                    }

                                    if (client.unreadMessages > 1) {
                                       text2 = "@gre@" + client.unreadMessages + " unread messages";
                                    }

                                    widget2.message = "You have " + text2 + "\\nin your message centre.";
                                 }
                              }
                           } else {
                              int friendCount = client.friendCount;
                              if (client.friendServerStatus != 2) {
                                 friendCount = 0;
                              }

                              if (contentType > 800) {
                                 contentType -= 701;
                              } else {
                                 contentType -= 101;
                              }

                              if (contentType >= friendCount) {
                                 widget2.message = "";
                                 widget2.optionType = 0;
                              } else {
                                 if (client.friendWorlds[contentType] != 0 && client.friendWorlds[contentType] == nodeID) {
                                    widget2.message = "@gre@World-1";
                                 } else {
                                    widget2.message = "@red@Offline";
                                 }

                                 widget2.optionType = 1;
                              }
                           }
                        } else if (contentType == 1 && client.friendServerStatus == 0) {
                           widget2.message = "Loading friend list";
                           widget2.optionType = 0;
                        } else if (contentType == 1 && client.friendServerStatus == 1) {
                           widget2.message = "Connecting to friendserver";
                           widget2.optionType = 0;
                        } else if (contentType == 2 && client.friendServerStatus != 2) {
                           widget2.message = "Please wait...";
                           widget2.optionType = 0;
                        } else {
                           int friendCount2 = client.friendCount;
                           if (client.friendServerStatus != 2) {
                              friendCount2 = 0;
                           }

                           if (contentType > 700) {
                              contentType -= 601;
                           } else {
                              contentType--;
                           }

                           if (contentType >= friendCount2) {
                              widget2.message = "";
                              widget2.optionType = 0;
                           } else {
                              widget2.message = client.friendNames[contentType];
                              widget2.optionType = 1;
                           }
                        }
                     }

                     if (widget3.type == 0) {
                        if (widget3.scrollPosition > widget3.scrollMax - widget3.height) {
                           widget3.scrollPosition = widget3.scrollMax - widget3.height;
                        }

                        if (widget3.scrollPosition < 0) {
                           widget3.scrollPosition = 0;
                        }

                        this.drawInterface(widget3.scrollPosition, top2, widget3, pixelIndex);
                        if (widget3.scrollMax > widget3.height) {
                           this.drawScrollBar(widget3.height, widget3.scrollPosition, pixelIndex, top2 + widget3.width, widget3.scrollMax, false);
                        }
                     } else if (widget3.type != 1) {
                        if (widget3.type == 2) {
                           int inventorySpriteXIndex = 0;

                           for (int loopIndex = 0; loopIndex < widget3.height; loopIndex++) {
                              for (int loopIndex2 = 0; loopIndex2 < widget3.width; loopIndex2++) {
                                 int scalar2 = top2 + loopIndex2 * (32 + widget3.spritePaddingX);
                                 int scalar3 = pixelIndex + loopIndex * (32 + widget3.spritePaddingY);
                                 if (inventorySpriteXIndex < 20) {
                                    scalar2 += widget3.inventorySpriteX[inventorySpriteXIndex];
                                    scalar3 += widget3.inventorySpriteY[inventorySpriteXIndex];
                                 }

                                 if (widget3.inventoryIds[inventorySpriteXIndex] > 0) {
                                    int localMouseX = 0;
                                    int localMouseY = 0;
                                    int key = widget3.inventoryIds[inventorySpriteXIndex] - 1;
                                    if (scalar2 > Rasterizer2D.topX - 32
                                          && scalar2 < Rasterizer2D.bottomX
                                          && scalar3 > Rasterizer2D.topY - 32
                                          && scalar3 < Rasterizer2D.bottomY
                                       || this.activeInterfaceType != 0 && this.draggedSlot == inventorySpriteXIndex) {
                                       int pixel = 0;
                                       if (this.itemSelected == 1 && this.selectedItemSlot == inventorySpriteXIndex && this.selectedItemWidgetId == widget3.id) {
                                          pixel = 16777215;
                                       }

                                       Sprite sprite;
                                       if ((sprite = ItemDefinition.getSprite(key, widget3.inventoryAmounts[inventorySpriteXIndex], pixel)) != null) {
                                          if (this.activeInterfaceType != 0 && this.draggedSlot == inventorySpriteXIndex && this.draggedWidgetId == widget3.id) {
                                             localMouseX = super.mouseX - this.dragStartX;
                                             localMouseY = super.mouseY - this.dragStartY;
                                             if (localMouseX < 5 && localMouseX > -5) {
                                                localMouseX = 0;
                                             }

                                             if (localMouseY < 5 && localMouseY > -5) {
                                                localMouseY = 0;
                                             }

                                             if (this.widgetDragDuration < 5) {
                                                localMouseX = 0;
                                                localMouseY = 0;
                                             }

                                             sprite.drawSpriteHalfAlpha(scalar2 + localMouseX, scalar3 + localMouseY);
                                             if (scalar3 + localMouseY < Rasterizer2D.topY && widget.scrollPosition > 0 && widget.parentId != 5292) {
                                                int localAnimationCycleDelta;
                                                if ((localAnimationCycleDelta = this.animationCycleDelta * (Rasterizer2D.topY - scalar3 - localMouseY) / 3) > this.animationCycleDelta * 10) {
                                                   localAnimationCycleDelta = this.animationCycleDelta * 10;
                                                }

                                                if (localAnimationCycleDelta > widget.scrollPosition) {
                                                   localAnimationCycleDelta = widget.scrollPosition;
                                                }

                                                widget.scrollPosition -= localAnimationCycleDelta;
                                                this.dragStartY += localAnimationCycleDelta;
                                             }

                                             if (scalar3 + localMouseY + 32 > Rasterizer2D.bottomY && widget.scrollPosition < widget.scrollMax - widget.height) {
                                                int animationCycleDelta2;
                                                if ((animationCycleDelta2 = this.animationCycleDelta * (scalar3 + localMouseY + 32 - Rasterizer2D.bottomY) / 3) > this.animationCycleDelta * 10) {
                                                   animationCycleDelta2 = this.animationCycleDelta * 10;
                                                }

                                                if (animationCycleDelta2 > widget.scrollMax - widget.height - widget.scrollPosition) {
                                                   animationCycleDelta2 = widget.scrollMax - widget.height - widget.scrollPosition;
                                                }

                                                widget.scrollPosition += animationCycleDelta2;
                                                this.dragStartY -= animationCycleDelta2;
                                             }
                                          } else if (this.atInventoryInterfaceType != 0 && this.atInventoryIndex == inventorySpriteXIndex && this.atInventoryInterface == widget3.id) {
                                             sprite.drawSpriteHalfAlpha(scalar2, scalar3);
                                          } else {
                                             sprite.drawSprite(scalar2, scalar3);
                                          }

                                          if ((sprite.canvasWidth == 33 || widget3.inventoryAmounts[inventorySpriteXIndex] != 1) && widget3.id != bankTabSummaryWidgetId) {
                                             int inventoryAmount = widget3.inventoryAmounts[inventorySpriteXIndex];
                                             int chatPrivilege = 0;
                                             if (inventoryAmount > 0) {
                                                chatPrivilege = 16776960;
                                             }

                                             if (inventoryAmount >= 100000) {
                                                chatPrivilege = 16777215;
                                             }

                                             if (inventoryAmount >= 10000000) {
                                                chatPrivilege = 65408;
                                             }

                                             this.smallFont.textLeft(0, formatAmountShort(inventoryAmount), scalar3 + 10 + localMouseY, scalar2 + 1 + localMouseX);
                                             this.smallFont.textLeft(chatPrivilege, formatAmountShort(inventoryAmount), scalar3 + 9 + localMouseY, scalar2 + localMouseX);
                                          }
                                       }
                                    }
                                 } else {
                                    Sprite sprite4;
                                    if (widget3.inventorySprites != null && inventorySpriteXIndex < 20 && (sprite4 = widget3.inventorySprites[inventorySpriteXIndex]) != null) {
                                       sprite4.drawSprite(scalar2, scalar3);
                                    }
                                 }

                                 inventorySpriteXIndex++;
                              }
                           }
                        } else if (widget3.type == 3) {
                           boolean flag = false;
                           if (this.chatHoverWidgetId == widget3.id || this.tabHoverWidgetId == widget3.id || this.viewportHoverWidgetId == widget3.id) {
                              flag = true;
                           }

                           int pixel2;
                           if (this.interfaceIsSelected(widget3)) {
                              pixel2 = widget3.secondaryColor;
                              if (flag && widget3.secondaryHoverColor != 0) {
                                 pixel2 = widget3.secondaryHoverColor;
                              }
                           } else {
                              pixel2 = widget3.textColor;
                              if (flag && widget3.defaultHoverColor != 0) {
                                 pixel2 = widget3.defaultHoverColor;
                              }
                           }

                           if (widget3.opacity == 0) {
                              if (widget3.filled) {
                                 if (screenMode != 0 && widget3.width >= 512 && widget3.height >= 334) {
                                    int sourceClientWidth = clientWidth;
                                    int sourceClientHeight = clientHeight;
                                    Rasterizer2D.setClip(clientHeight, 0, sourceClientWidth, 0);
                                    Rasterizer2D.fillRectangle(sourceClientHeight, 0, 0, pixel2, sourceClientWidth);
                                    Rasterizer2D.setRasterBuffer(height, width, pixels, depthBuffer);
                                    Rasterizer2D.setClip(bottomY, topX, bottomX, topY);
                                    if (!this.fullscreenInterfaceCoversViewport) {
                                       this.fullscreenInterfaceCoversViewport = true;
                                    }
                                 } else {
                                    Rasterizer2D.fillRectangle(widget3.height, pixelIndex, top2, pixel2, widget3.width);
                                 }
                              } else if (screenMode != 0 && widget3.width >= 512 && widget3.height >= 334) {
                                 int clientWidth2 = clientWidth;
                                 int clientHeight2 = clientHeight;
                                 Rasterizer2D.setClip(clientHeight, 0, clientWidth2, 0);
                                 Rasterizer2D.drawRectangle(0, clientWidth2, clientHeight2, pixel2, 0);
                                 Rasterizer2D.setRasterBuffer(height, width, pixels, depthBuffer);
                                 Rasterizer2D.setClip(bottomY, topX, bottomX, topY);
                              } else {
                                 Rasterizer2D.drawRectangle(top2, widget3.width, widget3.height, pixel2, pixelIndex);
                              }
                           } else if (widget3.filled) {
                              if (screenMode != 0 && widget3.width >= 512 && widget3.height >= 334) {
                                 int clientWidth3 = clientWidth;
                                 int clientHeight3 = clientHeight;
                                 Rasterizer2D.setClip(clientHeight, 0, clientWidth3, 0);
                                 Rasterizer2D.fillRectangleAlpha(pixel2, 0, clientWidth3, clientHeight3, 256 - (widget3.opacity & 255), 0);
                                 Rasterizer2D.setRasterBuffer(height, width, pixels, depthBuffer);
                                 Rasterizer2D.setClip(bottomY, topX, bottomX, topY);
                              } else {
                                 Rasterizer2D.fillRectangleAlpha(pixel2, pixelIndex, widget3.width, widget3.height, 256 - (widget3.opacity & 255), top2);
                              }
                           } else if (screenMode != 0 && widget3.width >= 512 && widget3.height >= 334) {
                              int clientWidth4 = clientWidth;
                              int clientHeight4 = clientHeight;
                              Rasterizer2D.setClip(clientHeight, 0, clientWidth4, 0);
                              Rasterizer2D.drawRectangleAlpha(0, clientHeight4, 256 - (widget3.opacity & 255), pixel2, widget3.width, 0);
                              Rasterizer2D.setRasterBuffer(height, width, pixels, depthBuffer);
                              Rasterizer2D.setClip(bottomY, topX, bottomX, topY);
                           } else {
                              Rasterizer2D.drawRectangleAlpha(pixelIndex, widget3.height, 256 - (widget3.opacity & 255), pixel2, widget3.width, top2);
                           }
                        } else if (widget3.type == 4) {
                           RichTextFont richTextFont = widget3.font;
                           String text3 = widget3.message;
                           boolean localFlag = false;
                           if (this.chatHoverWidgetId == widget3.id || this.tabHoverWidgetId == widget3.id || this.viewportHoverWidgetId == widget3.id) {
                              localFlag = true;
                           }

                           int secondaryColor2;
                           if (this.interfaceIsSelected(widget3)) {
                              secondaryColor2 = widget3.secondaryColor;
                              if (localFlag && widget3.secondaryHoverColor != 0) {
                                 secondaryColor2 = widget3.secondaryHoverColor;
                              }

                              if (widget3.secondaryText.length() > 0) {
                                 text3 = widget3.secondaryText;
                              }
                           } else {
                              secondaryColor2 = widget3.textColor;
                              if (localFlag && widget3.defaultHoverColor != 0) {
                                 secondaryColor2 = widget3.defaultHoverColor;
                              }
                           }

                           if (widget3.optionType == 6 && this.continuedDialogue) {
                              text3 = "Please wait...";
                              secondaryColor2 = widget3.textColor;
                           }

                           if (Rasterizer2D.width == activeChatRasterWidth) {
                              if (secondaryColor2 == 16776960) {
                                 secondaryColor2 = 255;
                              }

                              if (secondaryColor2 == 49152) {
                                 secondaryColor2 = 16777215;
                              }
                           }

                           if (screenMode != 0
                              && (this.backDialogID != -1 || this.dialogID != -1 || widget3.message.contains("Click here to continue"))
                              && (widget.id == this.backDialogID || widget.id == this.dialogID)) {
                              if (secondaryColor2 == 16776960) {
                                 secondaryColor2 = 255;
                              }

                              if (secondaryColor2 == 49152) {
                                 secondaryColor2 = 16777215;
                              }
                           }

                           for (int position2 = pixelIndex + richTextFont.lineHeight; text3.length() > 0; position2 += richTextFont.lineHeight) {
                              if (text3.indexOf("%") != -1) {
                                 int position;
                                 while ((position = text3.indexOf("%1")) != -1) {
                                    text3 = text3.substring(0, position) + formatInterfaceValue(this.executeInterfaceScript(widget3, 0)) + text3.substring(position + 2);
                                 }

                                 while ((position = text3.indexOf("%2")) != -1) {
                                    text3 = text3.substring(0, position) + formatInterfaceValue(this.executeInterfaceScript(widget3, 1)) + text3.substring(position + 2);
                                 }

                                 while ((position = text3.indexOf("%3")) != -1) {
                                    text3 = text3.substring(0, position) + formatInterfaceValue(this.executeInterfaceScript(widget3, 2)) + text3.substring(position + 2);
                                 }

                                 while ((position = text3.indexOf("%4")) != -1) {
                                    text3 = text3.substring(0, position) + formatInterfaceValue(this.executeInterfaceScript(widget3, 3)) + text3.substring(position + 2);
                                 }

                                 while ((position = text3.indexOf("%5")) != -1) {
                                    text3 = text3.substring(0, position) + formatInterfaceValue(this.executeInterfaceScript(widget3, 4)) + text3.substring(position + 2);
                                 }
                              }

                              int indexOf2;
                              String text4;
                              if ((indexOf2 = text3.indexOf("\\n")) != -1) {
                                 text4 = text3.substring(0, indexOf2);
                                 text3 = text3.substring(indexOf2 + 2);
                              } else {
                                 text4 = text3;
                                 text3 = "";
                              }

                              if (widget3.textAlignment == 1) {
                                 richTextFont.drawCenteredString(text4, top2 + widget3.width / 2, position2, secondaryColor2, widget3.textShadow ? 0 : -1);
                              } else if (widget3.textAlignment == 2) {
                                 richTextFont.drawBasicString(text4, top2 - richTextFont.getTextWidth(text4), position2, secondaryColor2, widget3.textShadow ? 0 : -1);
                              } else {
                                 richTextFont.drawBasicString(text4, top2, position2, secondaryColor2, widget3.textShadow ? 0 : -1);
                              }
                           }
                        } else if (widget3.type != 5 && widget3.type != 17 && widget3.type != 18 && widget3.type != 19) {
                           if (widget3.type == 6) {
                              int viewportCenterX2 = Rasterizer3D.viewportCenterX;
                              int viewportCenterY2 = Rasterizer3D.viewportCenterY;
                              Rasterizer3D.viewportCenterX = top2 + widget3.width / 2;
                              Rasterizer3D.viewportCenterY = pixelIndex + widget3.height / 2;
                              int right;
                              boolean flag4;
                              if (flag4 = this.interfaceIsSelected(widget3)) {
                                 right = widget3.enabledAnimationId;
                              } else {
                                 right = widget3.defaultAnimationId;
                              }

                              Model model2;
                              if (right == -1) {
                                 model2 = widget3.getAnimatedModel(-1, -1, flag4, -1, -1, -1);
                              } else {
                                 AnimationSequence animationSequence;
                                 right = (animationSequence = AnimationSequence.sequences[AnimationSequence.remapId(right)]).frameIds[widget3.nextAnimationFrame];
                                 int frameLength = animationSequence.frameLengths[widget3.animationFrame];
                                 int animationFrameCycle = widget3.animationFrameCycle;
                                 model2 = widget3.getAnimatedModel(animationSequence.secondaryFrameIds[widget3.animationFrame], animationSequence.frameIds[widget3.animationFrame], flag4, right, frameLength, animationFrameCycle);
                              }

                              boolean flag2 = false;
                              if (screenMode != 0 && widget3.parentId == 11877) {
                                 right = clientWidth;
                                 int clientHeight5 = clientHeight;
                                 Rasterizer2D.setClip(clientHeight, 0, right, 0);
                                 double viewportCenterY3 = Rasterizer3D.viewportCenterY / 2;
                                 Rasterizer3D.viewportCenterY = (int)(Rasterizer3D.viewportCenterY + viewportCenterY3);
                                 if (model2 != null) {
                                    model2.scale(384, 128, 384);
                                 }

                                 widget3.modelZoom = 180;
                                 flag2 = true;
                              }

                              if (screenMode != 0 && widget3.parentId == 13103) {
                                 right = clientWidth;
                                 int clientHeight6 = clientHeight;
                                 Rasterizer2D.setClip(clientHeight, 0, right, 0);
                                 if (model2 != null) {
                                    model2.scale(256, 128, 256);
                                 }

                                 widget3.modelZoom = 5;
                                 flag2 = true;
                              }

                              if (screenMode != 0 && widget3.parentId == 8677) {
                                 right = clientWidth;
                                 int clientHeight7 = clientHeight;
                                 Rasterizer2D.setClip(clientHeight, 0, right, 0);
                                 if (model2 != null) {
                                    model2.scale(256, 256, 128);
                                 }

                                 widget3.modelZoom = 65;
                                 flag2 = true;
                              }

                              boolean flag3 = false;
                              if (screenMode != 0
                                 && (this.backDialogID != -1 && widget3.parentId == this.backDialogID || this.dialogID != -1 && widget3.parentId == this.dialogID)) {
                                 int clientHeight8 = clientHeight - 31;
                                 int clientHeight9 = clientHeight - 158;
                                 Rasterizer2D.setClip(clientHeight8, 7, 511, clientHeight9);
                                 Rasterizer3D.setViewportFromClip();
                                 Rasterizer3D.viewportCenterX = top2 + widget3.width / 2 - 7;
                                 Rasterizer3D.viewportCenterY = pixelIndex + widget3.height / 2 - clientHeight9;
                                 flag3 = true;
                              }

                              int localSINE = Rasterizer3D.SINE[widget3.modelRotation1] * widget3.modelZoom >> 16;
                              int localCOSINE = Rasterizer3D.COSINE[widget3.modelRotation1] * widget3.modelZoom >> 16;
                              if (model2 != null) {
                                 model2.renderSimple(widget3.modelRotation2, 0, widget3.modelRotation1, 0, localSINE, localCOSINE);
                              }

                              Rasterizer3D.viewportCenterX = viewportCenterX2;
                              Rasterizer3D.viewportCenterY = viewportCenterY2;
                              if (flag3) {
                                 Rasterizer2D.setRasterBuffer(height, width, pixels, depthBuffer);
                                 Rasterizer2D.setClip(bottomY, topX, bottomX, topY);
                                 Rasterizer3D.viewportCenterX = viewportCenterX;
                                 Rasterizer3D.viewportCenterY = viewportCenterY;
                                 Rasterizer3D.scanOffsets = scanOffsets;
                              }

                              if (flag2) {
                                 Rasterizer2D.setRasterBuffer(height, width, pixels, depthBuffer);
                                 Rasterizer2D.setClip(bottomY, topX, bottomX, topY);
                              }
                           } else if (widget3.type == 7) {
                              RichTextFont font = widget3.font;
                              int inventoryIdIndex = 0;

                              for (int loopIndex3 = 0; loopIndex3 < widget3.height; loopIndex3++) {
                                 for (int loopIndex4 = 0; loopIndex4 < widget3.width; loopIndex4++) {
                                    if (widget3.inventoryIds[inventoryIdIndex] > 0) {
                                       ItemDefinition itemDefinition;
                                       String text5 = (itemDefinition = ItemDefinition.lookup(widget3.inventoryIds[inventoryIdIndex] - 1)).name;
                                       if (itemDefinition.stackable || widget3.inventoryAmounts[inventoryIdIndex] != 1) {
                                          text5 = text5 + " x" + formatAmountLong(widget3.inventoryAmounts[inventoryIdIndex]);
                                       }

                                       int scalar4 = top2 + loopIndex4 * (115 + widget3.spritePaddingX);
                                       int scalar5 = pixelIndex + loopIndex3 * (12 + widget3.spritePaddingY);
                                       if (widget3.textAlignment == 1) {
                                          font.drawCenteredString(text5, scalar4 + widget3.width / 2, scalar5, widget3.textColor, widget3.textShadow ? 0 : -1);
                                       } else if (widget3.textAlignment == 2) {
                                          font.drawBasicString(text5, scalar4 - font.getTextWidth(text5), scalar5, widget3.textColor, widget3.textShadow ? 0 : -1);
                                       } else {
                                          font.drawBasicString(text5, scalar4, scalar5, widget3.textColor, widget3.textShadow ? 0 : -1);
                                       }
                                    }

                                    inventoryIdIndex++;
                                 }
                              }
                           } else if (widget3.type == 8
                              && (this.chatTooltipWidgetId == widget3.id || this.tabTooltipWidgetId == widget3.id || this.viewportTooltipWidgetId == widget3.id)
                              && this.tooltipHoverTicks == this.tooltipDelayTicks
                              && !this.menuOpen) {
                              int sourceMeasuredTextWidth = 0;
                              int height2 = 0;
                              BitmapFont bitmapFont = this.plainFont;

                              for (String text6 = widget3.message; text6.length() > 0; height2 += bitmapFont.lineHeight + 1) {
                                 if (text6.indexOf("%") != -1) {
                                    int indexOf3;
                                    while ((indexOf3 = text6.indexOf("%1")) != -1) {
                                       text6 = text6.substring(0, indexOf3) + formatInterfaceValue(this.executeInterfaceScript(widget3, 0)) + text6.substring(indexOf3 + 2);
                                    }

                                    while ((indexOf3 = text6.indexOf("%2")) != -1) {
                                       text6 = text6.substring(0, indexOf3) + formatInterfaceValue(this.executeInterfaceScript(widget3, 1)) + text6.substring(indexOf3 + 2);
                                    }

                                    while ((indexOf3 = text6.indexOf("%3")) != -1) {
                                       text6 = text6.substring(0, indexOf3) + formatInterfaceValue(this.executeInterfaceScript(widget3, 2)) + text6.substring(indexOf3 + 2);
                                    }

                                    while ((indexOf3 = text6.indexOf("%4")) != -1) {
                                       text6 = text6.substring(0, indexOf3) + formatInterfaceValue(this.executeInterfaceScript(widget3, 3)) + text6.substring(indexOf3 + 2);
                                    }

                                    while ((indexOf3 = text6.indexOf("%5")) != -1) {
                                       text6 = text6.substring(0, indexOf3) + formatInterfaceValue(this.executeInterfaceScript(widget3, 4)) + text6.substring(indexOf3 + 2);
                                    }
                                 }

                                 int indexOf4;
                                 String text7;
                                 if ((indexOf4 = text6.indexOf("\\n")) != -1) {
                                    text7 = text6.substring(0, indexOf4);
                                    text6 = text6.substring(indexOf4 + 2);
                                 } else {
                                    text7 = text6;
                                    text6 = "";
                                 }

                                 int measuredTextWidth;
                                 if ((measuredTextWidth = bitmapFont.getTextWidth(text7)) > sourceMeasuredTextWidth) {
                                    sourceMeasuredTextWidth = measuredTextWidth;
                                 }
                              }

                              sourceMeasuredTextWidth += 6;
                              height2 += 7;
                              int scalar6 = top2 + widget3.width - 5 - sourceMeasuredTextWidth;
                              int pixelIndex2 = pixelIndex + widget3.height + 5;
                              if (scalar6 < top2 + 5) {
                                 scalar6 = top2 + 5;
                              }

                              if (scalar6 + sourceMeasuredTextWidth > top + widget.width) {
                                 scalar6 = top + widget.width - sourceMeasuredTextWidth;
                              }

                              if (pixelIndex2 + height2 > bottom + widget.height) {
                                 pixelIndex2 = pixelIndex - height2;
                              }

                              Rasterizer2D.fillRectangle(height2, pixelIndex2, scalar6, 16777120, sourceMeasuredTextWidth);
                              Rasterizer2D.drawRectangle(scalar6, sourceMeasuredTextWidth, height2, 0, pixelIndex2);
                              String text8 = widget3.message;

                              for (int position3 = pixelIndex2 + bitmapFont.lineHeight + 2; text8.length() > 0; position3 += bitmapFont.lineHeight + 1) {
                                 if (text8.indexOf("%") != -1) {
                                    int indexOf5;
                                    while ((indexOf5 = text8.indexOf("%1")) != -1) {
                                       text8 = text8.substring(0, indexOf5) + formatInterfaceValue(this.executeInterfaceScript(widget3, 0)) + text8.substring(indexOf5 + 2);
                                    }

                                    while ((indexOf5 = text8.indexOf("%2")) != -1) {
                                       text8 = text8.substring(0, indexOf5) + formatInterfaceValue(this.executeInterfaceScript(widget3, 1)) + text8.substring(indexOf5 + 2);
                                    }

                                    while ((indexOf5 = text8.indexOf("%3")) != -1) {
                                       text8 = text8.substring(0, indexOf5) + formatInterfaceValue(this.executeInterfaceScript(widget3, 2)) + text8.substring(indexOf5 + 2);
                                    }

                                    while ((indexOf5 = text8.indexOf("%4")) != -1) {
                                       text8 = text8.substring(0, indexOf5) + formatInterfaceValue(this.executeInterfaceScript(widget3, 3)) + text8.substring(indexOf5 + 2);
                                    }

                                    while ((indexOf5 = text8.indexOf("%5")) != -1) {
                                       text8 = text8.substring(0, indexOf5) + formatInterfaceValue(this.executeInterfaceScript(widget3, 4)) + text8.substring(indexOf5 + 2);
                                    }
                                 }

                                 String text9;
                                 int indexOf6;
                                 if ((indexOf6 = text8.indexOf("\\n")) != -1) {
                                    text9 = text8.substring(0, indexOf6);
                                    text8 = text8.substring(indexOf6 + 2);
                                 } else {
                                    text9 = text8;
                                    text8 = "";
                                 }

                                 if (widget3.textAlignment == 1) {
                                    bitmapFont.textCenterShadow(pixelIndex2, scalar6 + widget3.width / 2, text9, position3, false);
                                 } else if (text9.contains("\\r")) {
                                    String text10 = text9.substring(0, text9.indexOf("\\r"));
                                    String text11 = text9.substring(text9.indexOf("\\r") + 2);
                                    bitmapFont.textLeftShadow(false, scalar6 + 3, 0, text10, position3);
                                    top2 = sourceMeasuredTextWidth + scalar6 - bitmapFont.getTextWidth(text11) - 2;
                                    bitmapFont.textLeftShadow(false, top2, 0, text11, position3);
                                    System.out.println("Box: " + sourceMeasuredTextWidth);
                                 } else {
                                    bitmapFont.textLeftShadow(false, scalar6 + 3, 0, text9, position3);
                                 }
                              }
                           }
                        } else {
                           Sprite sprite2;
                           if (this.interfaceIsSelected(widget3)) {
                              sprite2 = widget3.enabledSprite;
                           } else {
                              sprite2 = widget3.disabledSprite;
                           }

                           if (sprite2 != null) {
                              if (this.spellSelected == 1 && widget3.id == selectedSpellHighlightWidgetId && selectedSpellHighlightWidgetId != 0) {
                                 sprite2.drawOutlinedSprite(top2, pixelIndex, 16777215);
                              } else {
                                 sprite2.drawSprite(top2, pixelIndex);
                              }
                           }

                           Sprite sprite3;
                           if (this.interfaceIsSelected(widget3)) {
                              sprite3 = widget3.alternateEnabledSprite;
                           } else {
                              sprite3 = widget3.alternateDisabledSprite;
                           }

                           if (sprite3 != null
                              && super.mouseX >= top2 + 4
                              && super.mouseX <= top2 + 4 + sprite3.spriteWidth
                              && super.mouseY >= pixelIndex + 4
                              && super.mouseY <= pixelIndex + 4 + sprite3.spriteHeight) {
                              sprite3.drawSprite(top2, pixelIndex);
                           }
                        }
                     }
                  }
               }
            }

            Rasterizer2D.setClip(bottomY, topX, bottomX, topY);
         }
      }
   }
   private void randomizeBackground(IndexedSprite indexedSprite) {
      for (int flameNoiseEntryIndex = 0; flameNoiseEntryIndex < this.flameNoise.length; flameNoiseEntryIndex++) {
         this.flameNoise[flameNoiseEntryIndex] = 0;
      }

      for (int loopIndex = 0; loopIndex < 5000; loopIndex++) {
         int flameNoiseIndex2 = (int)(Math.random() * 128.0 * 256.0);
         this.flameNoise[flameNoiseIndex2] = (int)(Math.random() * 256.0);
      }

      for (int loopIndex2 = 0; loopIndex2 < 20; loopIndex2++) {
         for (int loopIndex3 = 1; loopIndex3 < 255; loopIndex3++) {
            for (int loopIndex4 = 1; loopIndex4 < 127; loopIndex4++) {
               int flameNoiseScratchIndex = loopIndex4 + (loopIndex3 << 7);
               this.flameNoiseScratch[flameNoiseScratchIndex] = (this.flameNoise[flameNoiseScratchIndex - 1] + this.flameNoise[flameNoiseScratchIndex + 1] + this.flameNoise[flameNoiseScratchIndex - 128] + this.flameNoise[flameNoiseScratchIndex + 128]) / 4;
            }
         }

         int[] flameNoiseScratchOrFlameNoise = this.flameNoise;
         this.flameNoise = this.flameNoiseScratch;
         this.flameNoiseScratch = flameNoiseScratchOrFlameNoise;
      }

      if (indexedSprite != null) {
         int scalar = 0;

         for (int loopIndex5 = 0; loopIndex5 < indexedSprite.height; loopIndex5++) {
            for (int loopIndex6 = 0; loopIndex6 < indexedSprite.width; loopIndex6++) {
               if (indexedSprite.pixelIndices[scalar++] != 0) {
                  int flameNoiseIndex = loopIndex6 + 16 + indexedSprite.xOffset;
                  int scalar2 = loopIndex5 + 16 + indexedSprite.yOffset;
                  flameNoiseIndex += scalar2 << 7;
                  this.flameNoise[flameNoiseIndex] = 0;
               }
            }
         }
      }
   }
   private void updateCameraFollow() {
      try {
         int sourceCameraFocusX = localPlayer.worldX + this.cameraX;
         int sourceCameraFocusY = localPlayer.worldY + this.cameraY;
         if (this.cameraFocusX - sourceCameraFocusX < -500 || this.cameraFocusX - sourceCameraFocusX > 500 || this.cameraFocusY - sourceCameraFocusY < -500 || this.cameraFocusY - sourceCameraFocusY > 500) {
            this.cameraFocusX = sourceCameraFocusX;
            this.cameraFocusY = sourceCameraFocusY;
         }

         if (this.cameraFocusX != sourceCameraFocusX) {
            this.cameraFocusX = this.cameraFocusX + (sourceCameraFocusX - this.cameraFocusX) / 16;
         }

         if (this.cameraFocusY != sourceCameraFocusY) {
            this.cameraFocusY = this.cameraFocusY + (sourceCameraFocusY - this.cameraFocusY) / 16;
         }

         if (super.keyStatus[1] == 1) {
            this.cameraYawVelocity = this.cameraYawVelocity + (-24 - this.cameraYawVelocity) / 2;
         } else if (super.keyStatus[2] == 1) {
            this.cameraYawVelocity = this.cameraYawVelocity + (24 - this.cameraYawVelocity) / 2;
         } else {
            this.cameraYawVelocity /= 2;
         }

         if (super.keyStatus[3] == 1) {
            this.cameraPitchVelocity = this.cameraPitchVelocity + (12 - this.cameraPitchVelocity) / 2;
         } else if (super.keyStatus[4] == 1) {
            this.cameraPitchVelocity = this.cameraPitchVelocity + (-12 - this.cameraPitchVelocity) / 2;
         } else {
            this.cameraPitchVelocity /= 2;
         }

         this.minimapInt1 = this.minimapInt1 + this.cameraYawVelocity / 2 & 2047;
         this.cameraPitch = this.cameraPitch + this.cameraPitchVelocity / 2;
         if (this.cameraPitch < 128) {
            this.cameraPitch = 128;
         }

         if (this.cameraPitch > 383) {
            this.cameraPitch = 383;
         }

         sourceCameraFocusX = this.cameraFocusX >> 7;
         sourceCameraFocusY = this.cameraFocusY >> 7;
         int tileHeight = this.getTileHeight(this.plane, this.cameraFocusY, this.cameraFocusX);
         int sourcePlane = 0;
         if (sourceCameraFocusX > 3 && sourceCameraFocusY > 3 && sourceCameraFocusX < 100 && sourceCameraFocusY < 100) {
            for (int loopIndex = sourceCameraFocusX - 4; loopIndex <= sourceCameraFocusX + 4; loopIndex++) {
               for (int loopIndex2 = sourceCameraFocusY - 4; loopIndex2 <= sourceCameraFocusY + 4; loopIndex2++) {
                  int plane = this.plane;
                  if (this.plane < 3 && (this.byteGroundArray[1][loopIndex][loopIndex2] & 2) == 2) {
                     plane++;
                  }

                  if ((plane = tileHeight - this.intGroundArray[plane][loopIndex][loopIndex2]) > sourcePlane) {
                     sourcePlane = plane;
                  }
               }
            }
         }

         if (++cameraNoiseCounter > 1512) {
            cameraNoiseCounter = 0;
            this.outgoingBuffer.writeOpcode(77);
            this.outgoingBuffer.writeByte(0);
            int currentPosition = this.outgoingBuffer.currentPosition;
            this.outgoingBuffer.writeByte((int)(Math.random() * 256.0));
            this.outgoingBuffer.writeByte(101);
            this.outgoingBuffer.writeByte(233);
            this.outgoingBuffer.writeShort(45092);
            if ((int)(Math.random() * 2.0) == 0) {
               this.outgoingBuffer.writeShort(35784);
            }

            this.outgoingBuffer.writeByte((int)(Math.random() * 256.0));
            this.outgoingBuffer.writeByte(64);
            this.outgoingBuffer.writeByte(38);
            this.outgoingBuffer.writeShort((int)(Math.random() * 65536.0));
            this.outgoingBuffer.writeShort((int)(Math.random() * 65536.0));
            this.outgoingBuffer.writeLengthByte(this.outgoingBuffer.currentPosition - currentPosition);
         }

         int scalar;
         if ((scalar = sourcePlane * 192) > 98048) {
            scalar = 98048;
         }

         if (scalar < 32768) {
            scalar = 32768;
         }

         if (scalar > this.cameraDistanceScale) {
            this.cameraDistanceScale = this.cameraDistanceScale + (scalar - this.cameraDistanceScale) / 24;
         } else if (scalar < this.cameraDistanceScale) {
            this.cameraDistanceScale = this.cameraDistanceScale + (scalar - this.cameraDistanceScale) / 80;
         }
      } catch (Exception exception) {
         SignLink.reporterror(
            "glfc_ex "
               + localPlayer.worldX
               + ","
               + localPlayer.worldY
               + ","
               + this.cameraFocusX
               + ","
               + this.cameraFocusY
               + ","
               + this.mapRegionX
               + ","
               + this.mapRegionY
               + ","
               + this.baseX
               + ","
               + this.baseY
         );
         throw new RuntimeException("eek");
      }
   }
   @Override
   public final void showErrorScreen() {
      if (!this.loadingError) {
         sceneDrawCounter++;
         if (!loggedIn) {
            this.drawLoginScreen(false);
         } else {
            this.frameBuffer.initDrawingArea();
            this.drawGameScreen();
            this.frameBuffer.drawGraphics(0, super.graphics, 0);
         }

         this.scrollbarClickTicks = 0;
      } else {
         Client client = this;
         Graphics graphics;
         (graphics = this.getGameComponent().getGraphics()).setColor(Color.black);
         graphics.fillRect(0, 0, 765, 503);
         client.convertMidiVolumeToAttenuation(1);
         if (client.loadingError) {
            client.flameThreadRunning = false;
            graphics.setFont(new Font("Helvetica", 1, 16));
            graphics.setColor(Color.yellow);
            graphics.drawString("Sorry, an error has occured whilst loading RuneScape", 30, 35);
            graphics.setColor(Color.white);
            graphics.drawString("To fix this try the following (in order):", 30, 85);
            graphics.setColor(Color.white);
            graphics.setFont(new Font("Helvetica", 1, 12));
            graphics.drawString("1: Try closing ALL open web-browser windows, and reloading", 30, 135);
            graphics.drawString("2: Try clearing your web-browsers cache from tools->internet options", 30, 165);
            graphics.drawString("3: Try using a different game-world", 30, 195);
            graphics.drawString("4: Try rebooting your computer", 30, 225);
            graphics.drawString("5: Try selecting a different version of Java from the play-game menu", 30, 255);
         }
      }
   }
   private boolean isFriendOrSelf(String text) {
      if (text == null) {
         return false;
      }

      for (int friendNameIndex = 0; friendNameIndex < this.friendCount; friendNameIndex++) {
         if (text.equalsIgnoreCase(this.friendNames[friendNameIndex])) {
            return true;
         }
      }

      return text.equalsIgnoreCase(localPlayer.name);
   }
   private static String combatDiffColor(int combatLevel, int combatLevel2) {
      if ((combatLevel = combatLevel - combatLevel2) < -9) {
         return "@red@";
      } else if (combatLevel < -6) {
         return "@or3@";
      } else if (combatLevel < -3) {
         return "@or2@";
      } else if (combatLevel < 0) {
         return "@or1@";
      } else if (combatLevel > 9) {
         return "@gre@";
      } else if (combatLevel > 6) {
         return "@gr3@";
      } else if (combatLevel > 3) {
         return "@gr2@";
      } else {
         return combatLevel > 0 ? "@gr1@" : "@yel@";
      }
   }
   private static boolean shouldCenterInterface(Widget widget) {
      if (screenMode == 0) {
         return false;
      } else {
         return clientWidth >= 900 && clientHeight >= 650 ? true : widget != null && (widget.spriteXOffset != -1 || widget.spriteYOffset != -1);
      }
   }
   private String getCastleWarsInterfaceText(int widgetId) {
      if (Widget.widgets == null || widgetId < 0 || widgetId >= Widget.widgets.length
            || Widget.widgets[widgetId] == null || Widget.widgets[widgetId].message == null) {
         return "";
      }
      return Widget.widgets[widgetId].message;
   }

   private int getCastleWarsStatusColor(String status) {
      if (status == null) {
         return 16777215;
      }

      String lower = status.toLowerCase();
      if (lower.contains("safe") || lower.contains("cleared")
            || lower.contains("operational") || lower.contains("locked")) {
         return 65280;
      }
      if (lower.contains("taken") || lower.contains("destroyed")
            || lower.contains("collapsed") || lower.equals("health 0%")) {
         return 16724787;
      }
      if (lower.contains("dropped") || lower.contains("unlocked")) {
         return 16776960;
      }
      if (lower.startsWith("health ")) {
         try {
            int percentIndex = lower.indexOf('%');
            int health = Integer.parseInt(lower.substring(7, percentIndex).trim());
            if (health <= 25) {
               return 16724787;
            }
            if (health <= 60) {
               return 16776960;
            }
            return 65280;
         } catch (Exception ignored) {
         }
      }
      return 16777215;
   }

   private int parseCastleWarsCatapultAim(String text, int fallback) {
      try {
         int value = Integer.parseInt(text == null ? "" : text.trim());
         return Math.max(0, Math.min(30, value));
      } catch (Exception ignored) {
         return fallback;
      }
   }

   private void drawCastleWarsCatapultAimOverlay() {
      if (Widget.widgets == null || 11169 >= Widget.widgets.length
            || Widget.widgets[11169] == null) {
         return;
      }

      Widget root = Widget.widgets[11169];
      int baseX = 0;
      int baseY = 0;
      if (screenMode != 0 && shouldCenterInterface(root)) {
         baseX = clientWidth / 2 - 256;
         baseY = clientHeight / 2 - 167;
      }

      this.drawCastleWarsCatapultCoordinate(
            11301, this.castleWarsCatapultAimX, baseX, baseY);
      this.drawCastleWarsCatapultCoordinate(
            11302, this.castleWarsCatapultAimY, baseX, baseY);
   }

   private void drawCastleWarsCatapultCoordinate(
         int widgetId, int value, int baseX, int baseY) {
      if (widgetId < 0 || widgetId >= Widget.widgets.length
            || Widget.widgets[widgetId] == null) {
         return;
      }

      int[] position = this.findCastleWarsWidgetPosition(
            Widget.widgets[11169], widgetId, baseX, baseY, 0);
      if (position == null) {
         return;
      }

      Widget widget = Widget.widgets[widgetId];
      int width = widget.width > 0 ? widget.width : 86;
      int height = widget.height > 0 ? widget.height : 58;

      // The cache contains static sample digits (12 / 34). Cover only the
      // inside of their panel, keeping the native border, then draw the live
      // server-authoritative coordinate over it.
      int inset = 5;
      int drawWidth = Math.max(24, Math.min(92, width) - inset * 2);
      int drawHeight = Math.max(20, Math.min(62, height) - inset * 2);
      Rasterizer2D.fillRectangleAlternate(
            position[0] + inset, position[1] + inset,
            drawWidth, drawHeight, 0x8b7b59);

      String coordinate = value < 10 ? "0" + value : Integer.toString(value);
      int textX = position[0] + inset + drawWidth / 2;
      int textY = position[1] + inset + drawHeight / 2 + 5;
      this.boldFont.textCenter(0x302719, coordinate, textY, textX);
   }

   private int[] findCastleWarsWidgetPosition(int targetWidgetId) {
      if (Widget.widgets == null || 11344 >= Widget.widgets.length) {
         return null;
      }
      return this.findCastleWarsWidgetPosition(
            Widget.widgets[11344], targetWidgetId, 0, 0, 0);
   }

   private int[] findCastleWarsWidgetPosition(Widget parent, int targetWidgetId,
                                               int baseX, int baseY, int depth) {
      if (parent == null || parent.childIds == null
            || parent.childX == null || parent.childY == null || depth > 12) {
         return null;
      }

      for (int childIndex = 0; childIndex < parent.childIds.length; childIndex++) {
         int childId = parent.childIds[childIndex];
         if (childId < 0 || childId >= Widget.widgets.length) {
            continue;
         }

         Widget child = Widget.widgets[childId];
         if (child == null) {
            continue;
         }

         int childX = baseX + parent.childX[childIndex] + child.runtimeXOffset;
         int childY = baseY + parent.childY[childIndex]
               - parent.scrollPosition + child.runtimeYOffset;
         if (childId == targetWidgetId) {
            return new int[]{childX, childY};
         }

         if (child.type == 0) {
            int[] nested = this.findCastleWarsWidgetPosition(
                  child, targetWidgetId, childX, childY, depth + 1);
            if (nested != null) {
               return nested;
            }
         }
      }
      return null;
   }

   private void drawCastleWarsNativeIconLayer(int statusX, int statusY) {
      if (Widget.widgets == null || 11344 >= Widget.widgets.length
            || 11349 >= Widget.widgets.length) {
         return;
      }

      Widget root = Widget.widgets[11344];
      Widget anchorText = Widget.widgets[11349];
      int[] anchorPosition = this.findCastleWarsWidgetPosition(11349);
      if (root == null || anchorText == null || anchorPosition == null) {
         return;
      }

      int lineHeight = anchorText.font == null ? 12 : anchorText.font.lineHeight;
      int desiredTextX = statusX + 32;
      int nativeTextBaselineY = anchorPosition[1] + lineHeight;
      int drawX = desiredTextX - anchorPosition[0];
      int drawY = statusY - nativeTextBaselineY;

      // Draw the original cache-authored Castle Wars graphics, but suppress
      // its text because the relocated overlay draws the status strings itself.
      // The native widgets still evaluate configs 377/378, so the cache chooses
      // the proper Safe/Taken/Dropped, door, tunnel and catapult artwork.
      int[] textWidgetIds = new int[]{
            11345, 11346, 11349, 11350, 11352, 11353,
            11356, 11358, 11360, 11362, 11363, 11364, 11365, 11366
      };
      String[] messages = new String[textWidgetIds.length];
      String[] secondaryTexts = new String[textWidgetIds.length];

      for (int i = 0; i < textWidgetIds.length; i++) {
         int widgetId = textWidgetIds[i];
         if (widgetId >= 0 && widgetId < Widget.widgets.length
               && Widget.widgets[widgetId] != null) {
            messages[i] = Widget.widgets[widgetId].message;
            secondaryTexts[i] = Widget.widgets[widgetId].secondaryText;
            Widget.widgets[widgetId].message = "";
            Widget.widgets[widgetId].secondaryText = "";
         }
      }

      try {
         this.drawInterface(0, drawX, root, drawY);
      } finally {
         for (int i = 0; i < textWidgetIds.length; i++) {
            int widgetId = textWidgetIds[i];
            if (widgetId >= 0 && widgetId < Widget.widgets.length
                  && Widget.widgets[widgetId] != null) {
               Widget.widgets[widgetId].message = messages[i];
               Widget.widgets[widgetId].secondaryText = secondaryTexts[i];
            }
         }
      }
   }

   private int getCastleWarsRelocatedTextBaselineY(int widgetId,
                                                   int anchorBaselineY) {
      if (Widget.widgets == null || widgetId < 0 || widgetId >= Widget.widgets.length
            || 11349 >= Widget.widgets.length) {
         return anchorBaselineY;
      }

      Widget widget = Widget.widgets[widgetId];
      Widget anchor = Widget.widgets[11349];
      int[] widgetPosition = this.findCastleWarsWidgetPosition(widgetId);
      int[] anchorPosition = this.findCastleWarsWidgetPosition(11349);
      if (widget == null || anchor == null
            || widgetPosition == null || anchorPosition == null) {
         return anchorBaselineY;
      }

      int widgetLineHeight = widget.font == null ? 12 : widget.font.lineHeight;
      int anchorLineHeight = anchor.font == null ? 12 : anchor.font.lineHeight;
      int nativeWidgetBaselineY = widgetPosition[1] + widgetLineHeight;
      int nativeAnchorBaselineY = anchorPosition[1] + anchorLineHeight;
      return anchorBaselineY + nativeWidgetBaselineY - nativeAnchorBaselineY;
   }

   private void drawCastleWarsStatusLine(String label, int widgetId,
                                         int x, int y) {
      String status = this.getCastleWarsInterfaceText(widgetId);
      int textX = x + 32;
      this.richPlainFont.drawBasicString(label + ":", textX, y, 16777215, 0);
      int statusTextX = textX + this.richPlainFont.getTextWidth(label + ":") + 6;
      this.richBoldFont.drawBasicString(status, statusTextX, y,
            this.getCastleWarsStatusColor(status), 0);
   }

   private void drawCastleWarsGameOverlay() {
      String zamorakScore = this.getCastleWarsInterfaceText(11345);
      String saradominScore = this.getCastleWarsInterfaceText(11346);
      String timer = this.getCastleWarsInterfaceText(11353);
      int overlayWidth = screenMode == 0 ? 512 : clientWidth;
      int overlayHeight = screenMode == 0 ? 334 : clientHeight;

      // Keep the score high and centered inside the actual game viewport in
      // both fixed and resizable modes.
      this.richBoldFont.drawCenteredString(
            zamorakScore + "     " + saradominScore,
            overlayWidth / 2, 32, 16777215, 0);

      // Keep the team/objective state together at the left edge of the game
      // viewport without depending on the cache-authored child coordinates.
      int statusX = 10;
      int statusY = Math.max(120, overlayHeight / 2 - 100);
      this.drawCastleWarsNativeIconLayer(statusX, statusY);
      this.drawCastleWarsStatusLine("Zamorak flag", 11349, statusX,
            this.getCastleWarsRelocatedTextBaselineY(11349, statusY));
      this.drawCastleWarsStatusLine("Saradomin flag", 11350, statusX,
            this.getCastleWarsRelocatedTextBaselineY(11350, statusY));
      this.drawCastleWarsStatusLine("Main door", 11352, statusX,
            this.getCastleWarsRelocatedTextBaselineY(11352, statusY));
      this.drawCastleWarsStatusLine("Side door", 11356, statusX,
            this.getCastleWarsRelocatedTextBaselineY(11356, statusY));
      this.drawCastleWarsStatusLine("Tunnel 1", 11358, statusX,
            this.getCastleWarsRelocatedTextBaselineY(11358, statusY));
      this.drawCastleWarsStatusLine("Tunnel 2", 11360, statusX,
            this.getCastleWarsRelocatedTextBaselineY(11360, statusY));
      this.drawCastleWarsStatusLine("Catapult", 11362, statusX,
            this.getCastleWarsRelocatedTextBaselineY(11362, statusY));

      // Keep the clock inside the game viewport. In resizable mode it stays
      // left of the tab panel; in fixed mode it sits near the viewport's right edge.
      int timerX = screenMode == 0
            ? overlayWidth - 42
            : (this.resizableTabPanelVisible ? overlayWidth - 250 : overlayWidth - 55);
      int timerY = screenMode == 0
            ? 50
            : (this.resizableTabPanelVisible ? Math.max(80, overlayHeight - 290) : 50);
      this.richBoldFont.drawCenteredString(timer, timerX, timerY, 16777215, 0);
   }

   private void draw3dScreen() {
      Client client = this;
      if (this.splitpublicChat != 0) {
         BitmapFont bitmapFont = client.plainFont;
         int scalar = 0;
         if (client.systemUpdateTime != 0) {
            scalar = 1;
         }

         for (int chatMessageIndex = 0; chatMessageIndex < 100; chatMessageIndex++) {
            if (client.chatMessages[chatMessageIndex] != null) {
               int chatType = client.chatTypes[chatMessageIndex];
               String text = client.chatNames[chatMessageIndex];
               int chatPrivilege = client.chatPrivileges[chatMessageIndex];
               int chatDonatorStatuse = client.chatDonatorStatuses[chatMessageIndex];
               int chatAccountMode = client.chatAccountModes[chatMessageIndex];
               if ((chatType == 3 || chatType == 7) && (chatType == 7 || client.privateChatMode == 0 || client.privateChatMode == 1 && client.isFriendOrSelf(text))) {
                  int spriteDrawY = (screenMode == 0 ? 329 : clientHeight - 174) - scalar * 13;
                  bitmapFont.textLeft(0, "From", spriteDrawY, 4);
                  bitmapFont.textLeft(65535, "From", spriteDrawY - 1, 4);
                  int scalar2 = 4 + bitmapFont.getTextWidth("From ");
                  if (chatPrivilege == 1) {
                     client.moderatorIcons[0].drawBackground(scalar2, spriteDrawY - 12);
                     scalar2 += 14;
                     if (chatAccountMode != 0) {
                        client.gameModeIcons[chatAccountMode - 1].drawBackground(scalar2, spriteDrawY - 12);
                        scalar2 += 14;
                     }

                     if (chatDonatorStatuse == 1) {
                        client.moderatorIcons[2].drawBackground(scalar2, spriteDrawY - 12);
                        scalar2 += 14;
                     }
                  } else if (chatPrivilege == 2) {
                     client.moderatorIcons[1].drawBackground(scalar2, spriteDrawY - 12);
                     scalar2 += 14;
                  } else if (chatPrivilege == 0) {
                     if (chatAccountMode != 0) {
                        client.gameModeIcons[chatAccountMode - 1].drawBackground(scalar2, spriteDrawY - 12);
                        scalar2 += 14;
                     }

                     if (chatDonatorStatuse == 1) {
                        client.moderatorIcons[2].drawBackground(scalar2, spriteDrawY - 12);
                        scalar2 += 14;
                     }
                  }

                  bitmapFont.textLeft(0, text + ": " + client.chatMessages[chatMessageIndex], spriteDrawY, scalar2);
                  bitmapFont.textLeft(65535, text + ": " + client.chatMessages[chatMessageIndex], spriteDrawY - 1, scalar2);
                  if (++scalar >= 5) {
                     break;
                  }
               }

               if (chatType == 5 && client.privateChatMode < 2) {
                  int spriteDrawY2 = (screenMode == 0 ? 329 : clientHeight - 174) - scalar * 13;
                  bitmapFont.textLeft(0, client.chatMessages[chatMessageIndex], spriteDrawY2, 4);
                  bitmapFont.textLeft(65535, client.chatMessages[chatMessageIndex], spriteDrawY2 - 1, 4);
                  if (++scalar >= 5) {
                     break;
                  }
               }

               if (chatType == 6 && client.privateChatMode < 2) {
                  int spriteDrawY3 = (screenMode == 0 ? 329 : clientHeight - 174) - scalar * 13;
                  bitmapFont.textLeft(0, "To " + text + ": " + client.chatMessages[chatMessageIndex], spriteDrawY3, 4);
                  bitmapFont.textLeft(65535, "To " + text + ": " + client.chatMessages[chatMessageIndex], spriteDrawY3 - 1, 4);
                  if (++scalar >= 5) {
                     break;
                  }
               }
            }
         }
      }

      if (this.crossType == 1) {
         this.crosses[this.crossIndex / 100].drawSprite(this.crossX - 8 - 4, this.crossY - 8 - 4);
         if (++crossAnimationCounter > 67) {
            crossAnimationCounter = 0;
            this.outgoingBuffer.writeOpcode(78);
         }
      }

      if (this.crossType == 2) {
         this.crosses[4 + this.crossIndex / 100].drawSprite(this.crossX - 8 - 4, this.crossY - 8 - 4);
      }

      if (this.openWalkableInterface != -1) {
         Widget widget;
         if ((widget = Widget.widgets[this.openWalkableInterface]).width == 512 && widget.height == 334 && widget.type == 0 || widget.newScroller) {
            widget.width = screenMode == 0 ? 765 : clientWidth;
            widget.height = screenMode == 0 ? 503 : clientHeight;
            widget.newScroller = true;
         }

         this.animateInterface(this.animationCycleDelta, this.openWalkableInterface);
         this.centeredWalkableInterface = false;
         if (this.openWalkableInterface == 11344) {
            this.drawCastleWarsGameOverlay();
         } else if (screenMode != 0 && this.openWalkableInterface == 6673) {
            int left = clientWidth / 2 - 256;
            this.centeredWalkableInterface = true;
            this.drawInterface(0, left, Widget.widgets[this.openWalkableInterface], 20);
         } else if (screenMode != 0 && shouldCenterInterface(Widget.widgets[this.openWalkableInterface])) {
            int top = screenMode == 0 ? 0 : clientWidth / 2 - 256;
            int screenMode2 = screenMode == 0 ? 0 : clientHeight / 2 - 167;
            this.centeredWalkableInterface = true;
            this.drawInterface(0, top, Widget.widgets[this.openWalkableInterface], screenMode2);
         } else {
            this.drawInterface(0, 0, Widget.widgets[this.openWalkableInterface], 0);
         }
      }

      if (this.openInterfaceId != -1) {
         this.animateInterface(this.animationCycleDelta, this.openInterfaceId);
         if (screenMode != 0 && shouldCenterInterface(Widget.widgets[this.openInterfaceId])) {
            this.drawInterface(0, screenMode == 0 ? 0 : clientWidth / 2 - 256, Widget.widgets[this.openInterfaceId], screenMode == 0 ? 0 : clientHeight / 2 - 167);
         } else {
            this.drawInterface(0, 0, Widget.widgets[this.openInterfaceId], 0);
         }
         if (this.openInterfaceId == 11169) {
            this.drawCastleWarsCatapultAimOverlay();
         }
      }

      if (screenMode != 0) {
         if (this.fullscreenInterfaceBackdropVisible) {
            int localClientWidth = 0;
            int localClientHeight = 0;
            if (shouldCenterInterface(null)) {
               localClientWidth = clientWidth / 2 - 256;
               localClientHeight = clientHeight / 2 - 167;
            }

            if (this.openInterfaceId == 8016) {
               Rasterizer2D.fillRectangleAlternate(0, 0, localClientWidth + 12, clientHeight, 0);
               Rasterizer2D.fillRectangleAlternate(localClientWidth + 512 - 12, 0, clientWidth - (localClientWidth + 512 - 12), clientHeight, 0);
               Rasterizer2D.fillRectangleAlternate(localClientWidth, 0, 512, localClientHeight, 0);
               Rasterizer2D.fillRectangleAlternate(localClientWidth, localClientHeight + 334, 512, clientHeight - (localClientHeight + 334), 0);
            } else {
               Rasterizer2D.fillRectangleAlternate(0, 0, localClientWidth, clientHeight, 0);
               Rasterizer2D.fillRectangleAlternate(localClientWidth + 512, 0, clientWidth - (localClientWidth + 512), clientHeight, 0);
               Rasterizer2D.fillRectangleAlternate(localClientWidth, 0, 512, localClientHeight, 0);
               Rasterizer2D.fillRectangleAlternate(localClientWidth, localClientHeight + 334, 512, clientHeight - (localClientHeight + 334), 0);
            }
         }

         this.captureResizableUiBackground();
         this.drawChatArea();
         this.drawTabArea();
         this.drawMinimap();
         this.scaleResizableUiAfterRender();
      }

      client = this;
      this.onTutorialIsland = 0;
      int retrievedEntry = (localPlayer.worldX >> 7) + client.baseX;
      int localCombatLevel = (localPlayer.worldY >> 7) + client.baseY;
      if (retrievedEntry >= 3053 && retrievedEntry <= 3156 && localCombatLevel >= 3056 && localCombatLevel <= 3136) {
         client.onTutorialIsland = 1;
      }

      if (retrievedEntry >= 3072 && retrievedEntry <= 3118 && localCombatLevel >= 9492 && localCombatLevel <= 9535) {
         client.onTutorialIsland = 1;
      }

      if (client.onTutorialIsland == 1 && retrievedEntry >= 3139 && retrievedEntry <= 3199 && localCombatLevel >= 3008 && localCombatLevel <= 3062) {
         client.onTutorialIsland = 0;
      }

      if (!this.menuOpen) {
         this.processRightClick();
         this.drawTooltip();
      } else if (this.menuScreenArea == 0) {
         this.drawMenu();
      }

      if (this.multicombat == 1) {
         if (screenMode == 0) {
            this.multiOverlay.drawSprite(472, 296);
         } else {
            this.multiOverlay.drawSprite(clientWidth - 37, 174);
         }
      }

      if (skillBoxEnabled && this.openInterfaceId == -1) {
         this.drawChangedStatsOverlay();
      }

      if (wildernessLevelRangeEnabled && this.openWalkableInterface == 197) {
         Client client2 = this;
         int scalar3;
         retrievedEntry = (((scalar3 = (localPlayer.worldY >> 7) + client2.baseY) > 6400 ? scalar3 - 6400 : scalar3) - 3520) / 8 + 1;
         if ((localCombatLevel = localPlayer.combatLevel - retrievedEntry) < 3) {
            localCombatLevel = 3;
         }

         int combatLevel2;
         if ((combatLevel2 = localPlayer.combatLevel + retrievedEntry) > 126) {
            combatLevel2 = 126;
         }

         clientInstance.richPlainFont.drawCenteredString(localCombatLevel + " - " + combatLevel2, screenMode == 0 ? 485 : clientWidth - 336, screenMode == 0 ? 331 : 60, wildernessLevelRangeColor, 0);
      }

      if (fpsOn) {
         int chatPrivilege2 = 16776960;
         if (super.fps < 15) {
            chatPrivilege2 = 16711680;
         }

         this.plainFont.textRight("Fps:" + super.fps, screenMode == 0 ? 507 : clientWidth - 320, chatPrivilege2, 20);
         Runtime runtime;
         int scalar4 = (int)(((runtime = Runtime.getRuntime()).totalMemory() - runtime.freeMemory()) / 1024L);
         Calendar calendar;
         (calendar = Calendar.getInstance()).setTimeZone(this.timeZone);
         localCombatLevel = calendar.get(11);
         retrievedEntry = calendar.get(12);
         this.plainFont.textRight("Mem:" + scalar4 + "k", screenMode == 0 ? 507 : clientWidth - 320, 16776960, 35);
         this.plainFont.textRight("Mouse: " + this.mouseX + "," + this.mouseY, screenMode == 0 ? 507 : clientWidth - 320, 16776960, 50);
         String localText = localCombatLevel < 10 ? "0" + localCombatLevel : String.valueOf(localCombatLevel);
         String text2 = retrievedEntry < 10 ? "0" + retrievedEntry : String.valueOf(retrievedEntry);
         this.plainFont.textRight("Server time: " + localText + ":" + text2, screenMode == 0 ? 507 : clientWidth - 320, 16776960, 65);
      }

      if (this.systemUpdateTime != 0) {
         int scalar5 = (retrievedEntry = this.systemUpdateTime / 50) / 60;
         localCombatLevel = screenMode != 0 ? clientHeight - 165 : 329;
         if ((retrievedEntry = retrievedEntry % 60) < 10) {
            this.plainFont.textLeft(16776960, "System update in: " + scalar5 + ":0" + retrievedEntry, localCombatLevel, 4);
         } else {
            this.plainFont.textLeft(16776960, "System update in: " + scalar5 + ":" + retrievedEntry, localCombatLevel, 4);
         }

         if (++systemUpdateNoiseCounter > 75) {
            systemUpdateNoiseCounter = 0;
            this.outgoingBuffer.writeOpcode(148);
         }
      }
   }
   private void addIgnore(long ignoreListAsLong) {
      try {
         if (ignoreListAsLong != 0L) {
            if (this.ignoreCount >= 100) {
               this.pushMessage("Your ignore list is full. Max of 100 hit", 0, "", 0, 0, 0);
            } else {
               String text = NameUtils.formatDisplayName(NameUtils.decodeBase37(ignoreListAsLong));

               for (int ignoreListAsLongIndex = 0; ignoreListAsLongIndex < this.ignoreCount; ignoreListAsLongIndex++) {
                  if (this.ignoreListAsLongs[ignoreListAsLongIndex] == ignoreListAsLong) {
                     this.pushMessage(text + " is already on your ignore list", 0, "", 0, 0, 0);
                     return;
                  }
               }

               for (int friendEncodedNameIndex = 0; friendEncodedNameIndex < this.friendCount; friendEncodedNameIndex++) {
                  if (this.friendEncodedNames[friendEncodedNameIndex] == ignoreListAsLong) {
                     this.pushMessage("Please remove " + text + " from your friend list first", 0, "", 0, 0, 0);
                     return;
                  }
               }

               this.ignoreListAsLongs[this.ignoreCount++] = ignoreListAsLong;
               this.needDrawTabArea = true;
               this.outgoingBuffer.writeOpcode(133);
               this.outgoingBuffer.writeLong(ignoreListAsLong);
            }
         }
      } catch (RuntimeException exception) {
         SignLink.reporterror("45688, " + ignoreListAsLong + ", 4" + ", " + exception.toString());
         throw new RuntimeException();
      }
   }
   private void updatePlayerInstances() {
      for (int playerIndex2 = -1; playerIndex2 < this.playerCount; playerIndex2++) {
         int playerIndex;
         if (playerIndex2 == -1) {
            playerIndex = 2047;
         } else {
            playerIndex = this.playerIndices[playerIndex2];
         }

         Player player;
         if ((player = this.players[playerIndex]) != null) {
            this.updateActor(player);
         }
      }
   }
   private void processSpawnedObjects() {
      if (this.loadingStage == 2) {
         for (SpawnedObject spawnedObject = (SpawnedObject)this.spawns.first(); spawnedObject != null; spawnedObject = (SpawnedObject)this.spawns.next()) {
            if (spawnedObject.longevity > 0) {
               spawnedObject.longevity--;
            }

            if (spawnedObject.longevity == 0) {
               if (spawnedObject.previousId < 0 || RegionBuilder.isObjectTypeReady(spawnedObject.previousId, spawnedObject.previousType)) {
                  this.updateWorldObject(spawnedObject.y, spawnedObject.plane, spawnedObject.previousOrientation, spawnedObject.previousType, spawnedObject.x, spawnedObject.group, spawnedObject.previousId);
                  spawnedObject.unlink();
               }
            } else {
               if (spawnedObject.delay > 0) {
                  spawnedObject.delay--;
               }

               if (spawnedObject.delay == 0
                  && spawnedObject.x > 0
                  && spawnedObject.y > 0
                  && spawnedObject.x <= 102
                  && spawnedObject.y <= 102
                  && (spawnedObject.id < 0 || RegionBuilder.isObjectTypeReady(spawnedObject.id, spawnedObject.type))) {
                  this.updateWorldObject(spawnedObject.y, spawnedObject.plane, spawnedObject.orientation, spawnedObject.type, spawnedObject.x, spawnedObject.group, spawnedObject.id);
                  spawnedObject.delay = -1;
                  if (spawnedObject.id == spawnedObject.previousId && spawnedObject.previousId == -1) {
                     spawnedObject.unlink();
                  } else if (spawnedObject.id == spawnedObject.previousId && spawnedObject.orientation == spawnedObject.previousOrientation && spawnedObject.type == spawnedObject.previousType) {
                     spawnedObject.unlink();
                  }
               }
            }
         }
      }
   }
   private void determineMenuSize() {
      int menuWidthOrBoldFont = this.boldFont.getTextWidth("Choose Option");

      for (int menuActionNameIndex = 0; menuActionNameIndex < this.menuActionCount; menuActionNameIndex++) {
         int localRichBoldFont;
         if ((localRichBoldFont = this.richBoldFont.getTextWidth(this.menuActionNames[menuActionNameIndex])) > menuWidthOrBoldFont) {
            menuWidthOrBoldFont = localRichBoldFont;
         }
      }

      menuWidthOrBoldFont += 8;
      int scalar = 15 * this.menuActionCount + 21;
      if (this.fullscreenInterfaceId == -1 && screenMode == 0) {
         if (super.clickX > 4 && super.clickY > 4 && super.clickX < 516 && super.clickY < 338) {
            int menuOffsetXOrClickX;
            if ((menuOffsetXOrClickX = super.clickX - 4 - menuWidthOrBoldFont / 2) + menuWidthOrBoldFont > 512) {
               menuOffsetXOrClickX = 512 - menuWidthOrBoldFont;
            }

            if (menuOffsetXOrClickX < 0) {
               menuOffsetXOrClickX = 0;
            }

            int menuOffsetYOrClickY;
            if ((menuOffsetYOrClickY = super.clickY - 4) + scalar > 334) {
               menuOffsetYOrClickY = 334 - scalar;
            }

            if (menuOffsetYOrClickY < 0) {
               menuOffsetYOrClickY = 0;
            }

            this.menuOpen = true;
            this.menuScreenArea = 0;
            this.menuOffsetX = menuOffsetXOrClickX;
            this.menuOffsetY = menuOffsetYOrClickY;
            this.menuWidth = menuWidthOrBoldFont;
            this.menuHeight = 15 * this.menuActionCount + 22;
         }

         if (gameframeVersion != 474) {
            if (super.clickX > 553 && super.clickY > 205 && super.clickX < 743 && super.clickY < 466) {
               int sourceMenuOffsetX;
               if ((sourceMenuOffsetX = super.clickX - 553 - menuWidthOrBoldFont / 2) < 0) {
                  sourceMenuOffsetX = 0;
               } else if (sourceMenuOffsetX + menuWidthOrBoldFont > activeSidebarRasterWidth) {
                  sourceMenuOffsetX = activeSidebarRasterWidth - menuWidthOrBoldFont;
               }

               int sourceMenuOffsetY;
               if ((sourceMenuOffsetY = super.clickY - 205) < 0) {
                  sourceMenuOffsetY = 0;
               } else if (sourceMenuOffsetY + scalar > 261) {
                  sourceMenuOffsetY = 261 - scalar;
               }

               this.menuOpen = true;
               this.menuScreenArea = 1;
               this.menuOffsetX = sourceMenuOffsetX;
               this.menuOffsetY = sourceMenuOffsetY;
               this.menuWidth = menuWidthOrBoldFont;
               this.menuHeight = 15 * this.menuActionCount + 22;
            }
         } else if (super.clickX > 547 && super.clickY > 205 && super.clickX < 743 && super.clickY < 466) {
            int menuOffsetXOrClickX2;
            if ((menuOffsetXOrClickX2 = super.clickX - 547 - menuWidthOrBoldFont / 2) < 0) {
               menuOffsetXOrClickX2 = 0;
            } else if (menuOffsetXOrClickX2 + menuWidthOrBoldFont > activeSidebarRasterWidth) {
               menuOffsetXOrClickX2 = activeSidebarRasterWidth - menuWidthOrBoldFont;
            }

            int menuOffsetYOrClickY2;
            if ((menuOffsetYOrClickY2 = super.clickY - 205) < 0) {
               menuOffsetYOrClickY2 = 0;
            } else if (menuOffsetYOrClickY2 + scalar > 261) {
               menuOffsetYOrClickY2 = 261 - scalar;
            }

            this.menuOpen = true;
            this.menuScreenArea = 1;
            this.menuOffsetX = menuOffsetXOrClickX2;
            this.menuOffsetY = menuOffsetYOrClickY2;
            this.menuWidth = menuWidthOrBoldFont;
            this.menuHeight = 15 * this.menuActionCount + 22;
         }

         if (gameframeVersion != 474) {
            if (super.clickX > 17 && super.clickY > 357 && super.clickX < 496 && super.clickY < 453) {
               int menuOffsetXOrClickX3;
               if ((menuOffsetXOrClickX3 = super.clickX - 17 - menuWidthOrBoldFont / 2) < 0) {
                  menuOffsetXOrClickX3 = 0;
               } else if (menuOffsetXOrClickX3 + menuWidthOrBoldFont > activeChatRasterWidth) {
                  menuOffsetXOrClickX3 = activeChatRasterWidth - menuWidthOrBoldFont;
               }

               int menuOffsetYOrClickY3;
               if ((menuOffsetYOrClickY3 = super.clickY - 357) < 0) {
                  menuOffsetYOrClickY3 = 0;
               } else if (menuOffsetYOrClickY3 + scalar > activeChatRasterHeight) {
                  menuOffsetYOrClickY3 = activeChatRasterHeight - scalar;
               }

               this.menuOpen = true;
               this.menuScreenArea = 2;
               this.menuOffsetX = menuOffsetXOrClickX3;
               this.menuOffsetY = menuOffsetYOrClickY3;
               this.menuWidth = menuWidthOrBoldFont;
               this.menuHeight = 15 * this.menuActionCount + 22;
               return;
            }
         } else if (super.clickX > 7 && super.clickY > 345 && super.clickX < 512 && super.clickY < 500) {
            int menuOffsetXOrClickX4;
            if ((menuOffsetXOrClickX4 = super.clickX - 7 - menuWidthOrBoldFont / 2) < 0) {
               menuOffsetXOrClickX4 = 0;
            } else if (menuOffsetXOrClickX4 + menuWidthOrBoldFont > activeChatRasterWidth) {
               menuOffsetXOrClickX4 = activeChatRasterWidth - menuWidthOrBoldFont;
            }

            int menuOffsetYOrClickY4;
            if ((menuOffsetYOrClickY4 = super.clickY - 345) < 0) {
               menuOffsetYOrClickY4 = 0;
            } else if (menuOffsetYOrClickY4 + scalar > activeChatRasterHeight) {
               menuOffsetYOrClickY4 = activeChatRasterHeight - scalar;
            }

            this.menuOpen = true;
            this.menuScreenArea = 2;
            this.menuOffsetX = menuOffsetXOrClickX4;
            this.menuOffsetY = menuOffsetYOrClickY4;
            this.menuWidth = menuWidthOrBoldFont;
            this.menuHeight = 15 * this.menuActionCount + 22;
            return;
         }
      } else {
         int menuClickX = this.getMenuClickX();
         int menuClickY = this.getMenuClickY();
         if (menuClickX <= 0 || menuClickY <= 0 || menuClickX >= clientWidth || menuClickY >= clientHeight) {
            return;
         }

         int menuOffsetXOrClickX5;
         if ((menuOffsetXOrClickX5 = menuClickX - menuWidthOrBoldFont / 2) + menuWidthOrBoldFont > clientWidth) {
            menuOffsetXOrClickX5 = clientWidth - menuWidthOrBoldFont;
         }

         if (menuOffsetXOrClickX5 < 0) {
            menuOffsetXOrClickX5 = 0;
         }

         int menuOffsetYOrClickY5 = menuClickY;
         if (menuClickY + scalar > clientHeight) {
            menuOffsetYOrClickY5 = clientHeight - scalar;
         }

         if (menuOffsetYOrClickY5 < 0) {
            menuOffsetYOrClickY5 = 0;
         }

         this.menuOpen = true;
         this.menuScreenArea = 0;
         this.menuOffsetX = menuOffsetXOrClickX5;
         this.menuOffsetY = menuOffsetYOrClickY5;
         this.menuWidth = menuWidthOrBoldFont;
         this.menuHeight = 15 * this.menuActionCount + 22;
      }
   }
   private void unloadTitleScreen() {
      this.flameThreadRunning = false;

      while (this.drawingFlames) {
         this.flameThreadRunning = false;

         try {
            Thread.sleep(50L);
         } catch (Exception exception) {
         }
      }

      this.titleBox = null;
      this.titleButton = null;
      this.titleRuneSprites = null;
      this.activeFlamePalette = null;
      this.warmFlamePalette = null;
      this.greenFlamePalette = null;
      this.blueFlamePalette = null;
      this.flameNoise = null;
      this.flameNoiseScratch = null;
      this.flameIntensity = null;
      this.flameIntensityScratch = null;
      this.originalFlameRightBackground = null;
      this.originalBottomLeftBackground = null;
   }
   private boolean animateInterface(int animationCycleDelta, int widgetIndex2) {
      boolean flag = false;
      Widget widget;
      int[] values;
      int scalar = (values = (widget = Widget.widgets[widgetIndex2]).childIds).length;

      int widgetIndex;
      for (int position = 0; position < scalar && (widgetIndex = values[position]) != -1; position++) {
         Widget widget2;
         if ((widget2 = Widget.widgets[widgetIndex]).type == 1) {
            flag |= this.animateInterface(animationCycleDelta, widget2.id);
         }

         if (widget2.type == 6 && (widget2.defaultAnimationId != -1 || widget2.enabledAnimationId != -1)) {
            int localEnabledAnimationId;
            if (this.interfaceIsSelected(widget2)) {
               localEnabledAnimationId = widget2.enabledAnimationId;
            } else {
               localEnabledAnimationId = widget2.defaultAnimationId;
            }

            if (localEnabledAnimationId != -1) {
               AnimationSequence animationSequence = AnimationSequence.sequences[AnimationSequence.remapId(localEnabledAnimationId)];

               for (widget2.animationFrameCycle += animationCycleDelta; widget2.animationFrameCycle > animationSequence.getFrameLength(widget2.animationFrame); flag = true) {
                  widget2.animationFrameCycle = widget2.animationFrameCycle - (animationSequence.getFrameLength(widget2.animationFrame) + 1);
                  widget2.animationFrame++;
                  if (widget2.animationFrame >= animationSequence.frameCount) {
                     widget2.animationFrame = widget2.animationFrame - animationSequence.frameStep;
                     if (widget2.animationFrame < 0 || widget2.animationFrame >= animationSequence.frameCount) {
                        widget2.animationFrame = 0;
                     }
                  }
               }

               if (smoothAnimations) {
                  widget2.nextAnimationFrame = widget2.animationFrame + 1;
                  if (widget2.nextAnimationFrame >= animationSequence.frameCount) {
                     widget2.nextAnimationFrame = 0;
                  }
               }
            }
         }
      }

      return flag;
   }
   private int getCameraPlane() {
      if (hideRoofs) {
         return this.plane;
      }

      int localPlane = 3;
      if (this.zCameraPos < 310) {
         int position = this.cameraPositionX >> 7;
         int position2 = this.xCameraPos >> 7;
         int localWorldX = localPlayer.worldX >> 7;
         int localWorldY = localPlayer.worldY >> 7;
         if ((this.byteGroundArray[this.plane][position][position2] & 4) != 0) {
            localPlane = this.plane;
         }

         int scalar;
         if (localWorldX > position) {
            scalar = localWorldX - position;
         } else {
            scalar = position - localWorldX;
         }

         int scalar2;
         if (localWorldY > position2) {
            scalar2 = localWorldY - position2;
         } else {
            scalar2 = position2 - localWorldY;
         }

         if (scalar > scalar2) {
            scalar = (scalar2 << 16) / scalar;
            scalar2 = 32768;

            while (position != localWorldX) {
               if (position < localWorldX) {
                  position++;
               } else if (position > localWorldX) {
                  position--;
               }

               if ((this.byteGroundArray[this.plane][position][position2] & 4) != 0) {
                  localPlane = this.plane;
               }

               if ((scalar2 += scalar) >= 65536) {
                  scalar2 -= 65536;
                  if (position2 < localWorldY) {
                     position2++;
                  } else if (position2 > localWorldY) {
                     position2--;
                  }

                  if ((this.byteGroundArray[this.plane][position][position2] & 4) != 0) {
                     localPlane = this.plane;
                  }
               }
            }
         } else {
            scalar = (scalar << 16) / scalar2;
            scalar2 = 32768;

            while (position2 != localWorldY) {
               if (position2 < localWorldY) {
                  position2++;
               } else if (position2 > localWorldY) {
                  position2--;
               }

               if ((this.byteGroundArray[this.plane][position][position2] & 4) != 0) {
                  localPlane = this.plane;
               }

               if ((scalar2 += scalar) >= 65536) {
                  scalar2 -= 65536;
                  if (position < localWorldX) {
                     position++;
                  } else if (position > localWorldX) {
                     position--;
                  }

                  if ((this.byteGroundArray[this.plane][position][position2] & 4) != 0) {
                     localPlane = this.plane;
                  }
               }
            }
         }
      }

      if ((this.byteGroundArray[this.plane][localPlayer.worldX >> 7][localPlayer.worldY >> 7] & 4) != 0) {
         localPlane = this.plane;
      }

      return localPlane;
   }
   private int getRoofPlane() {
      if (hideRoofs) {
         return this.plane;
      } else {
         return this.getTileHeight(this.plane, this.xCameraPos, this.cameraPositionX) - this.cameraPositionZ < 800
               && (this.byteGroundArray[this.plane][this.cameraPositionX >> 7][this.xCameraPos >> 7] & 4) != 0
            ? this.plane
            : 3;
      }
   }
   private void removeIgnore(long encodedName) {
      try {
         if (encodedName != 0L) {
            for (int ignoreListAsLongIndex = 0; ignoreListAsLongIndex < this.ignoreCount; ignoreListAsLongIndex++) {
               if (this.ignoreListAsLongs[ignoreListAsLongIndex] == encodedName) {
                  this.ignoreCount--;
                  this.needDrawTabArea = true;
                  System.arraycopy(this.ignoreListAsLongs, ignoreListAsLongIndex + 1, this.ignoreListAsLongs, ignoreListAsLongIndex, this.ignoreCount - ignoreListAsLongIndex);
                  this.outgoingBuffer.writeOpcode(74);
                  this.outgoingBuffer.writeLong(encodedName);
                  return;
               }
            }
         }
      } catch (RuntimeException exception) {
         SignLink.reporterror("47229, 3, " + encodedName + ", " + exception.toString());
         throw new RuntimeException();
      }
   }

   @Override
   public String getParameter(String text) {
      return super.getParameter(text);
   }
   private int executeInterfaceScript(Widget widget, int scriptCompareValueIndex) {
      if (widget.valueIndexArray != null && scriptCompareValueIndex < widget.valueIndexArray.length) {
         try {
            int[] valueIndexArrayEntry = widget.valueIndexArray[scriptCompareValueIndex];
            scriptCompareValueIndex = 0;
            int position = 0;
            byte byteCode = 0;

            while (true) {
               int scalar = valueIndexArrayEntry[position++];
               int localCurrentStats = 0;
               byte byteCode2 = 0;
               if (scalar == 0) {
                  return scriptCompareValueIndex;
               }

               if (scalar == 1) {
                  localCurrentStats = this.currentStats[valueIndexArrayEntry[position++]];
               }

               if (scalar == 2) {
                  localCurrentStats = this.maxStats[valueIndexArrayEntry[position++]];
               }

               if (scalar == 3) {
                  localCurrentStats = this.currentExp[valueIndexArrayEntry[position++]];
               }

               if (scalar == 4) {
                  Widget widget3 = Widget.widgets[valueIndexArrayEntry[position++]];
                  int scalar2;
                  if ((scalar2 = valueIndexArrayEntry[position++]) >= 0 && scalar2 < ItemDefinition.definitionCount && (!ItemDefinition.lookup(scalar2).members || isMembers)) {
                     for (int inventoryIdIndex = 0; inventoryIdIndex < widget3.inventoryIds.length; inventoryIdIndex++) {
                        if (widget3.inventoryIds[inventoryIdIndex] == scalar2 + 1) {
                           localCurrentStats += widget3.inventoryAmounts[inventoryIdIndex];
                        }
                     }
                  }
               }

               if (scalar == 5) {
                  localCurrentStats = this.varps[valueIndexArrayEntry[position++]];
               }

               if (scalar == 6) {
                  localCurrentStats = experienceTable[this.maxStats[valueIndexArrayEntry[position++]] - 1];
               }

               if (scalar == 7) {
                  localCurrentStats = this.varps[valueIndexArrayEntry[position++]] * 100 / 46875;
               }

               if (scalar == 8) {
                  localCurrentStats = localPlayer.combatLevel;
               }

               if (scalar == 9) {
                  for (int enabledIndex = 0; enabledIndex < 25; enabledIndex++) {
                     if (Skills.enabled[enabledIndex]) {
                        localCurrentStats += this.maxStats[enabledIndex];
                     }
                  }
               }

               if (scalar == 10) {
                  Widget widget2 = Widget.widgets[valueIndexArrayEntry[position++]];
                  int scalar3;
                  if ((scalar3 = valueIndexArrayEntry[position++] + 1) >= 0 && scalar3 < ItemDefinition.definitionCount && (!ItemDefinition.lookup(scalar3).members || isMembers)) {
                     int[] inventoryIds = widget2.inventoryIds;
                     int inventoryIdsLengthOrInventoryIds = widget2.inventoryIds.length;

                     for (int inventoryIdIndex2 = 0; inventoryIdIndex2 < inventoryIdsLengthOrInventoryIds; inventoryIdIndex2++) {
                        int inventoryId;
                        if ((inventoryId = inventoryIds[inventoryIdIndex2]) == scalar3) {
                           localCurrentStats = 999999999;
                           break;
                        }
                     }
                  }
               }

               if (scalar == 11) {
                  localCurrentStats = this.energy;
               }

               if (scalar == 12) {
                  localCurrentStats = this.weight;
               }

               if (scalar == 13) {
                  int varp = this.varps[valueIndexArrayEntry[position++]];
                  int scalar4 = valueIndexArrayEntry[position++];
                  localCurrentStats = (varp & 1 << scalar4) == 0 ? 0 : 1;
               }

               if (scalar == 14) {
                  int definitionIndex = valueIndexArrayEntry[position++];
                  VarbitDefinition varbitDefinition;
                  int varpIndex = (varbitDefinition = VarbitDefinition.definitions[definitionIndex]).index;
                  definitionIndex = varbitDefinition.leastSignificantBit;
                  int mostSignificantBit = varbitDefinition.mostSignificantBit;
                  int bitMask = bitMasks[mostSignificantBit - definitionIndex];
                  localCurrentStats = this.varps[varpIndex] >> definitionIndex & bitMask;
               }

               if (scalar == 15) {
                  byteCode2 = 1;
               }

               if (scalar == 16) {
                  byteCode2 = 2;
               }

               if (scalar == 17) {
                  byteCode2 = 3;
               }

               if (scalar == 18) {
                  localCurrentStats = (localPlayer.worldX >> 7) + this.baseX;
               }

               if (scalar == 19) {
                  localCurrentStats = (localPlayer.worldY >> 7) + this.baseY;
               }

               if (scalar == 20) {
                  localCurrentStats = valueIndexArrayEntry[position++];
               }

               if (byteCode2 == 0) {
                  if (byteCode == 0) {
                     scriptCompareValueIndex += localCurrentStats;
                  }

                  if (byteCode == 1) {
                     scriptCompareValueIndex -= localCurrentStats;
                  }

                  if (byteCode == 2 && localCurrentStats != 0) {
                     scriptCompareValueIndex /= localCurrentStats;
                  }

                  if (byteCode == 3) {
                     scriptCompareValueIndex *= localCurrentStats;
                  }

                  byteCode = 0;
               } else {
                  byteCode = byteCode2;
               }
            }
         } catch (Exception exception) {
            return -1;
         }
      } else {
         return -2;
      }
   }
   private void drawTooltip() {
      if (this.menuActionCount >= 2 || this.itemSelected != 0 || this.spellSelected != 0) {
         String text;
         if (this.itemSelected == 1 && this.menuActionCount < 2) {
            text = "Use " + this.selectedItemName + " with...";
         } else if (this.spellSelected == 1 && this.menuActionCount < 2) {
            text = this.spellTooltip + "...";
         } else {
            int menuActionNameIndex;
            if (priorityMenuActionIndex != -1) {
               menuActionNameIndex = priorityMenuActionIndex;
            } else {
               menuActionNameIndex = this.menuActionCount - 1;
            }

            text = this.menuActionNames[menuActionNameIndex];
         }

         if (this.menuActionCount > 2) {
            text = text + "@whi@ / " + (this.menuActionCount - 2) + " more options";
         }

         this.richBoldFont.drawBasicString(text, 4, 15, 16777215, 0);
      }
   }
   private void processRunOrbHover() {
      int localScreenMode = screenMode == 0 ? 548 : clientWidth - 204;
      int screenMode2 = screenMode == 0 ? 124 : 133;
      if (super.mouseX >= localScreenMode && super.mouseX <= localScreenMode + 57 && super.mouseY >= screenMode2 && super.mouseY < screenMode2 + 33) {
         if (!this.runEnabled) {
            this.menuActionNames[1] = "Toggle Run";
         } else if (this.runEnabled) {
            this.menuActionNames[1] = "Toggle Run";
         }

         this.runOrbHovered = true;
         this.menuActionIds[1] = 1050;
         this.menuActionCount = 2;
      } else {
         this.runOrbHovered = false;
      }
   }
   private static int getStatusOrbTextColor(int energy) {
      if (energy >= 75) {
         return 65280;
      } else if (energy >= 50 && energy <= 74) {
         return 16776960;
      } else {
         return energy >= 25 && energy <= 49 ? 16750623 : 16711680;
      }
   }
   private void drawStatusOrbs() {
      BitmapFont bitmapFont = this.smallFont;
      int localScreenMode = screenMode == 0 ? 1 : clientWidth - 235;
      int screenMode2 = screenMode == 0 ? 38 : 47;
      customSprites[86].drawSprite(localScreenMode, screenMode2);
      customSprites[this.poisoned ? 76 : 75].drawSprite(localScreenMode + 27, screenMode2 + 4);
      customSprites[74].spriteHeight = 26 - (int)((double)this.currentStats[3] / this.maxStats[3] * 26.0);
      customSprites[74].drawSprite(localScreenMode + 27, screenMode2 + 4);
      int energy;
      if ((energy = (int)((double)this.currentStats[3] / this.maxStats[3] * 100.0)) > 20 || gameCycle % 20 < 10) {
         customSprites[82].drawSprite(localScreenMode + 27, screenMode2 + 4);
      }

      bitmapFont.textCenterShadow(getStatusOrbTextColor(energy), localScreenMode + 15, "" + this.currentStats[3], screenMode2 + 27, true);
      localScreenMode = screenMode == 0 ? 1 : clientWidth - 235;
      screenMode2 = screenMode == 0 ? 84 : 93;
      customSprites[86].drawSprite(localScreenMode, screenMode2);
      customSprites[78].drawSprite(localScreenMode + 27, screenMode2 + 4);
      customSprites[74].spriteHeight = 26 - (int)((double)this.currentStats[5] / this.maxStats[5] * 26.0);
      customSprites[74].drawSprite(localScreenMode + 27, screenMode2 + 4);
      customSprites[83].drawSprite(localScreenMode + 27, screenMode2 + 4);
      energy = (int)((double)this.currentStats[5] / this.maxStats[5] * 100.0);
      bitmapFont.textCenterShadow(getStatusOrbTextColor(energy), localScreenMode + 15, "" + this.currentStats[5], screenMode2 + 27, true);
      localScreenMode = screenMode == 0 ? 32 : clientWidth - 204;
      screenMode2 = screenMode == 0 ? 120 : 129;
      customSprites[this.runOrbHovered ? 87 : 86].drawSprite(localScreenMode, screenMode2);
      customSprites[this.runEnabled ? 80 : 79].drawSprite(localScreenMode + 27, screenMode2 + 4);
      customSprites[74].spriteHeight = (int)(26.0 - this.energy / 100.0 * 26.0);
      customSprites[74].drawSprite(localScreenMode + 27, screenMode2 + 4);
      customSprites[this.runEnabled ? 85 : 84].drawSprite(localScreenMode + 27, screenMode2 + 4);
      bitmapFont.textCenterShadow(getStatusOrbTextColor(this.energy), localScreenMode + 15, "" + this.energy, screenMode2 + 27, true);
   }
   private void drawMinimap() {
      if (screenMode == 0) {
         this.minimapImageProducer.initDrawingArea();
      }

      byte byteCode = 0;
      if (gameframeVersion == 474 && orbsEnabled) {
         byteCode = 29;
      }

      if (this.minimapState == 2) {
         if (screenMode == 0) {
            byte[] pixelIndices = mapBack.pixelIndices;
            int[] pixels = Rasterizer2D.pixels;
            int pixelIndicesLength = pixelIndices.length;

            for (int pixelIndex = 0; pixelIndex < pixelIndicesLength; pixelIndex++) {
               if (pixelIndices[pixelIndex] == 0) {
                  if (gameframeVersion == 474 && orbsEnabled) {
                     pixels[Rasterizer2D.toPixelIndex(pixelIndex)] = 0;
                  } else {
                     pixels[pixelIndex] = 0;
                  }
               }
            }
         }

         if (screenMode != 0) {
            customSprites[96].drawArgbSprite(clientWidth - 234, 0);
            customSprites[95].drawArgbSprite(clientWidth - 241, 0);
         }

         compassSprite.drawRotatedMasked(33, this.minimapInt1, compassMaskLineWidths, 256, compassMaskLineOffsets, 25, screenMode == 0 ? 0 : 9, screenMode == 0 ? byteCode + 0 : clientWidth - 207, 33, 25);
         if (orbsEnabled) {
            this.drawStatusOrbs();
         }

         if (screenMode == 0) {
            this.gameScreenImageProducer.initDrawingArea();
            return;
         }
      } else {
         int scalar = this.minimapInt1 + this.minimapInt2 & 2047;
         int localWorldX = 48 + localPlayer.worldX / 32;
         int worldX2 = 464 - localPlayer.worldY / 32;
         this.minimapDrawX = screenMode == 0 ? byteCode + 25 : clientWidth - 182;
         this.minimapDrawY = screenMode == 0 ? 5 : 14;
         this.minimapImage.drawRotatedMasked(150, scalar, minimapMaskLineWidths, 256 + this.minimapInt3, minimapMaskLineOffsets, worldX2, this.minimapDrawY, this.minimapDrawX, 146, localWorldX);
         compassSprite.drawRotatedMasked(33, this.minimapInt1, compassMaskLineWidths, 256, compassMaskLineOffsets, 25, screenMode == 0 ? 0 : 9, screenMode == 0 ? byteCode + 0 : clientWidth - 207, 33, 25);

         for (int minimapHintXIndex = 0; minimapHintXIndex < this.mapFunctionCount; minimapHintXIndex++) {
            worldX2 = (this.minimapHintX[minimapHintXIndex] << 2) + 2 - localPlayer.worldX / 32;
            scalar = (this.minimapHintY[minimapHintXIndex] << 2) + 2 - localPlayer.worldY / 32;
            this.drawMinimapIcon(this.minimapHint[minimapHintXIndex], worldX2, scalar);
         }

         for (int loopIndex = 0; loopIndex < 104; loopIndex++) {
            for (int loopIndex2 = 0; loopIndex2 < 104; loopIndex2++) {
               if (this.groundItems[this.plane][loopIndex][loopIndex2] != null) {
                  localWorldX = (loopIndex << 2) + 2 - localPlayer.worldX / 32;
                  scalar = (loopIndex2 << 2) + 2 - localPlayer.worldY / 32;
                  this.drawMinimapIcon(this.mapDotItem, localWorldX, scalar);
               }
            }
         }

         for (int npcIndex = 0; npcIndex < this.npcCount; npcIndex++) {
            Npc npc;
            if ((npc = this.npcs[this.npcIndices[npcIndex]]) != null && npc.isVisible()) {
               NpcDefinition npcDefinition = npc.definition;
               if (npc.definition.childIds != null) {
                  npcDefinition = npcDefinition.morph();
               }

               if (npcDefinition != null && npcDefinition.drawMinimapDot && npcDefinition.clickable) {
                  localWorldX = npc.worldX / 32 - localPlayer.worldX / 32;
                  scalar = npc.worldY / 32 - localPlayer.worldY / 32;
                  this.drawMinimapIcon(this.mapDotNpc, localWorldX, scalar);
               }
            }
         }

         for (int playerIndex = 0; playerIndex < this.playerCount; playerIndex++) {
            Player player;
            if ((player = this.players[this.playerIndices[playerIndex]]) != null && player.visible) {
               scalar = player.worldX / 32 - localPlayer.worldX / 32;
               localWorldX = player.worldY / 32 - localPlayer.worldY / 32;
               boolean flag = false;
               long encodedName = NameUtils.encodeBase37(player.name);
               boolean localFlag = false;
               if (player.name.toLowerCase().startsWith("mod ")) {
                  localFlag = true;
               }

               for (int friendEncodedNameIndex = 0; friendEncodedNameIndex < this.friendCount; friendEncodedNameIndex++) {
                  if (encodedName == this.friendEncodedNames[friendEncodedNameIndex] && this.friendWorlds[friendEncodedNameIndex] != 0) {
                     flag = true;
                     break;
                  }
               }

               boolean flag2 = false;
               if (localPlayer.team != 0 && player.team != 0 && localPlayer.team == player.team) {
                  flag2 = true;
               }

               boolean castleWarsPlayer = localPlayer.castleWarsTeam && player.castleWarsTeam;
               if (!localFlag) {
                  if (castleWarsPlayer) {
                     if (localPlayer.team == player.team) {
                        this.drawMinimapIcon(this.mapDotFriend, scalar, localWorldX);
                     } else {
                        this.drawMinimapIcon(this.mapDotNPC, scalar, localWorldX);
                     }
                  } else if (flag) {
                     this.drawMinimapIcon(this.mapDotPlayer, scalar, localWorldX);
                  } else if (flag2) {
                     this.drawMinimapIcon(this.mapDotFriend, scalar, localWorldX);
                  } else {
                     this.drawMinimapIcon(this.mapDotNPC, scalar, localWorldX);
                  }
               } else {
                  this.drawMinimapIcon(this.mapDotTeam, scalar, localWorldX);
               }
            }
         }

         if (this.hintIconDrawType != 0 && gameCycle % 20 < 10) {
            Npc npc2;
            if (this.hintIconDrawType == 1 && this.hintIconNpcId >= 0 && this.hintIconNpcId < this.npcs.length && (npc2 = this.npcs[this.hintIconNpcId]) != null) {
               worldX2 = npc2.worldX / 32 - localPlayer.worldX / 32;
               scalar = npc2.worldY / 32 - localPlayer.worldY / 32;
               this.drawMinimapHint(this.mapMarker, scalar, worldX2);
            }

            if (this.hintIconDrawType == 2) {
               int scalar2 = (this.hintIconX - this.baseX << 2) + 2 - localPlayer.worldX / 32;
               worldX2 = (this.hintIconY - this.baseY << 2) + 2 - localPlayer.worldY / 32;
               this.drawMinimapHint(this.mapMarker, worldX2, scalar2);
            }

            Player player2;
            if (this.hintIconDrawType == 10 && this.hintIconPlayerId >= 0 && this.hintIconPlayerId < this.players.length && (player2 = this.players[this.hintIconPlayerId]) != null) {
               worldX2 = player2.worldX / 32 - localPlayer.worldX / 32;
               scalar = player2.worldY / 32 - localPlayer.worldY / 32;
               this.drawMinimapHint(this.mapMarker, scalar, worldX2);
            }
         }

         if (this.destX != 0) {
            int scalar3 = (this.destX << 2) + 2 - localPlayer.worldX / 32;
            worldX2 = (this.destY << 2) + 2 - localPlayer.worldY / 32;
            this.drawMinimapIcon(this.mapFlag, scalar3, worldX2);
         }

         Rasterizer2D.fillRectangle(3, screenMode == 0 ? 78 : 87, screenMode == 0 ? byteCode + 97 : clientWidth - 110, 16777215, 3);
         if (screenMode != 0) {
            customSprites[95].drawArgbSprite(clientWidth - 241, 0);
         }

         if (orbsEnabled) {
            this.drawStatusOrbs();
         }

         if (screenMode == 0) {
            this.gameScreenImageProducer.initDrawingArea();
         }
      }
   }
   private void projectActorToScreen(Actor actor, int height) {
      this.calcEntityScreenPos(actor.worldX, height, actor.worldY);
   }
   private static void playMidiTrack(int newCurrentMusicVolume, byte[] midiData, boolean flag) {
      if (midiPlayer != null) {
         if (currentMusicVolume >= 0) {
            midiPlayer.stop();
            currentMusicVolume = -1;
            pendingMusicData = null;
            musicFadeTicksRemaining = 20;
            musicFadePosition = 0;
         }

         if (midiData != null) {
            if (musicFadeTicksRemaining > 0) {
               midiPlayer.resetVolume(newCurrentMusicVolume);
               musicFadeTicksRemaining = 0;
            }

            currentMusicVolume = newCurrentMusicVolume;
            midiPlayer.play(newCurrentMusicVolume, midiData, flag);
         }
      }
   }
   static final void shutdownMidiPlayer() {
      if (midiPlayer != null) {
         boolean flag = false;
         playMidiTrack(0, null, flag);
         if (musicFadeTicksRemaining > 0) {
            midiPlayer.resetVolume(256);
            musicFadeTicksRemaining = 0;
         }

         midiPlayer.closeResources();
         midiPlayer = null;
      }
   }
   private void calcEntityScreenPos(int scalarArgument, int newGetTileHeight, int worldY) {
      if (scalarArgument >= 128 && worldY >= 128 && scalarArgument <= 13056 && worldY <= 13056) {
         newGetTileHeight = this.getTileHeight(this.plane, worldY, scalarArgument) - newGetTileHeight;
         scalarArgument -= this.cameraPositionX;
         newGetTileHeight -= this.cameraPositionZ;
         worldY -= this.xCameraPos;
         int sINEEntry = Model.SINE[this.zCameraPos];
         int cOSINEEntry = Model.COSINE[this.zCameraPos];
         int SINE2 = Model.SINE[this.yCameraPos];
         int COSINE2 = Model.COSINE[this.yCameraPos];
         int scalar = worldY * SINE2 + scalarArgument * COSINE2 >> 16;
         worldY = worldY * COSINE2 - scalarArgument * SINE2 >> 16;
         scalarArgument = scalar;
         scalar = newGetTileHeight * cOSINEEntry - worldY * sINEEntry >> 16;
         if ((worldY = newGetTileHeight * sINEEntry + worldY * cOSINEEntry >> 16) >= 50) {
            this.spriteDrawX = Rasterizer3D.viewportCenterX + (scalarArgument << getProjectionScaleShift()) / worldY;
            this.spriteDrawY = Rasterizer3D.viewportCenterY + (scalar << getProjectionScaleShift()) / worldY;
         } else {
            this.spriteDrawX = -1;
            this.spriteDrawY = -1;
         }
      } else {
         this.spriteDrawX = -1;
         this.spriteDrawY = -1;
      }
   }
   private void scheduleSpawnedObject(int newLongevity, int newId, int newOrientation, int newGroup, int newY, int newType, int newPlane, int newX, int newDelay) {
      SpawnedObject spawnedObject = null;

      for (SpawnedObject sourceSpawnedObject = (SpawnedObject)this.spawns.first(); sourceSpawnedObject != null; sourceSpawnedObject = (SpawnedObject)this.spawns.next()) {
         if (sourceSpawnedObject.plane == newPlane && sourceSpawnedObject.x == newX && sourceSpawnedObject.y == newY && sourceSpawnedObject.group == newGroup) {
            spawnedObject = sourceSpawnedObject;
            break;
         }
      }

      if (spawnedObject == null) {
         (spawnedObject = new SpawnedObject()).plane = newPlane;
         spawnedObject.group = newGroup;
         spawnedObject.x = newX;
         spawnedObject.y = newY;
         this.captureSpawnedObjectState(spawnedObject);
         this.spawns.addLast(spawnedObject);
      }

      spawnedObject.id = newId;
      spawnedObject.type = newType;
      spawnedObject.orientation = newOrientation;
      spawnedObject.delay = newDelay;
      spawnedObject.longevity = newLongevity;
   }
   public static int packRgb(int parsedNumber, int parsedNumber2, int parsedNumber3) {
      return parsedNumber << 16 | parsedNumber2 << 8 | parsedNumber3;
   }
   private boolean interfaceIsSelected(Widget widget) {
      if (widget.scriptCompareTypes == null) {
         return false;
      }

      for (int scriptCompareValueIndex = 0; scriptCompareValueIndex < widget.scriptCompareTypes.length; scriptCompareValueIndex++) {
         int localExecuteInterfaceScript = this.executeInterfaceScript(widget, scriptCompareValueIndex);
         int expectedScriptValue = widget.scriptCompareValues[scriptCompareValueIndex];
         if (widget.scriptCompareTypes[scriptCompareValueIndex] == 2) {
            if (localExecuteInterfaceScript >= expectedScriptValue) {
               return false;
            }
         } else if (widget.scriptCompareTypes[scriptCompareValueIndex] == 3) {
            if (localExecuteInterfaceScript <= expectedScriptValue) {
               return false;
            }
         } else if (widget.scriptCompareTypes[scriptCompareValueIndex] == 4) {
            if (localExecuteInterfaceScript == expectedScriptValue) {
               return false;
            }
         } else if (localExecuteInterfaceScript != expectedScriptValue) {
            return false;
         }
      }

      return true;
   }
   private DataInputStream openJagGrabInputStream(String text) throws IOException {
      if (this.jaggrabSocket != null) {
         try {
            this.jaggrabSocket.close();
         } catch (Exception exception) {
         }

         this.jaggrabSocket = null;
      }

      this.jaggrabSocket = this.openSocket(43595);
      this.jaggrabSocket.setSoTimeout(10000);
      InputStream inputStream = this.jaggrabSocket.getInputStream();
      this.jaggrabSocket.getOutputStream().write(("JAGGRAB /" + text + "\n\n").getBytes());
      return new DataInputStream(inputStream);
   }
   private void drawLoginScreen(boolean flag) {
      this.setupLoginScreenBuffers();
      this.loginMusicImageProducer.initDrawingArea();
      customSprites[musicVolumeSetting > 0 ? 43 : 44].drawSprite(163, 198);
      this.flameLeftBackground.initDrawingArea();
      this.titleBox.drawBackground(0, 0);
      if (this.loginScreenState == 0) {
         this.smallFont.textCenterShadow(7711145, 180, this.onDemandFetcher.statusString, 180, true);
         this.boldFont.textCenterShadow(16776960, 180, "Welcome to RuneScape", 80, true);
         this.titleButton.drawBackground(27, 100);
         this.boldFont.textCenterShadow(16777215, 100, "New User", 125, true);
         this.titleButton.drawBackground(187, 100);
         this.boldFont.textCenterShadow(16777215, 260, "Existing User", 125, true);
      }

      if (this.loginScreenState == 2) {
         if (this.loginMessage1.length() > 0) {
            this.boldFont.textCenterShadow(16776960, 180, this.loginMessage1, 45, true);
            this.boldFont.textCenterShadow(16776960, 180, this.loginMessage2, 60, true);
         } else {
            this.boldFont.textCenterShadow(16776960, 180, this.loginMessage2, 53, true);
         }

         this.boldFont.textLeftShadow(true, 90, 16777215, "Username: " + username + (this.loginScreenCursorPos == 0 & gameCycle % 40 < 20 ? "@yel@|" : ""), 90);
         this.boldFont
            .textLeftShadow(true, 92, 16777215, "Password: " + NameUtils.mask(password) + (this.loginScreenCursorPos == 1 & gameCycle % 40 < 20 ? "@yel@|" : ""), 105);
         if (!flag) {
            this.titleButton.drawBackground(27, 130);
            this.boldFont.textCenterShadow(16777215, 100, "Login", 155, true);
            this.titleButton.drawBackground(187, 130);
            this.boldFont.textCenterShadow(16777215, 260, "Cancel", 155, true);
         }
      }

      if (this.loginScreenState == 3) {
         this.boldFont.textCenterShadow(16776960, 180, "Create a free account", 40, true);
         this.boldFont.textCenterShadow(16777215, 180, "To create a new account you need to", 65, true);
         this.boldFont.textCenterShadow(16777215, 180, "go back to the main RuneScape webpage", 80, true);
         this.boldFont.textCenterShadow(16777215, 180, "and choose the red 'create account'", 95, true);
         this.boldFont.textCenterShadow(16777215, 180, "button at the top right of that page.", 110, true);
         this.titleButton.drawBackground(107, 130);
         this.boldFont.textCenterShadow(16777215, 180, "Cancel", 155, true);
      }

      this.flameLeftBackground.drawGraphics(171, super.graphics, 202);
      if (this.welcomeScreenRaised) {
         this.welcomeScreenRaised = false;
         this.topLeft1BackgroundTile.drawGraphics(0, super.graphics, 128);
         this.bottomLeft1BackgroundTile.drawGraphics(371, super.graphics, 202);
         this.bottomRightImageProducer.drawGraphics(265, super.graphics, 0);
         this.loginMusicImageProducer.drawGraphics(265, super.graphics, 562);
         this.middleLeft1BackgroundTile.drawGraphics(171, super.graphics, 128);
         this.middleRightBackgroundBuffer.drawGraphics(171, super.graphics, 562);
      }

      if (autoLogin && !this.autoLoginAttempted) {
         this.autoLoginAttempted = true;
         if (username != null && !username.equals("") && password != null && !password.equals("")) {
            this.loginFailures = 0;
            this.login(username, password, false);
         }
      }
   }
   @Override
   public final void raiseWelcomeScreen() {
      this.welcomeScreenRaised = true;
   }
   public static Client getClient() {
      if (clientInstance == null) {
         clientInstance = new Client();
      }

      return clientInstance;
   }
   private void parseRegionPackets(Buffer newBuffer, int pktType) {
      if (pktType == 84) {
         int decodedUnsignedByte = newBuffer.readUnsignedByte();
         int localLocalX = this.localX + (decodedUnsignedByte >> 4 & 7);
         int localLocalY = this.localY + (decodedUnsignedByte & 7);
         int decodedUnsignedShort = newBuffer.readUnsignedShort();
         int readUnsignedShort2 = newBuffer.readUnsignedShort();
         int quantityOrReadUnsignedShort = newBuffer.readUnsignedShort();
         NodeDeque nodeDeque;
         if (localLocalX >= 0 && localLocalY >= 0 && localLocalX < 104 && localLocalY < 104 && (nodeDeque = this.groundItems[this.plane][localLocalX][localLocalY]) != null) {
            for (GroundItem groundItem = (GroundItem)nodeDeque.first(); groundItem != null; groundItem = (GroundItem)nodeDeque.next()) {
               if (groundItem.id == (decodedUnsignedShort & 32767) && groundItem.quantity == readUnsignedShort2) {
                  groundItem.quantity = quantityOrReadUnsignedShort;
                  break;
               }
            }

            this.spawnGroundItem(localLocalX, localLocalY);
            return;
         }
      } else {
         if (pktType == 105) {
            int soundTypeOrReadUnsignedByte = newBuffer.readUnsignedByte();
            int localX2 = this.localX + (soundTypeOrReadUnsignedByte >> 4 & 7);
            int localY2 = this.localY + (soundTypeOrReadUnsignedByte & 7);
            int soundOrReadUnsignedShort = newBuffer.readUnsignedShort();
            int readUnsignedByte2;
            int scalar = (readUnsignedByte2 = newBuffer.readUnsignedByte()) >> 4 & 15;
            soundTypeOrReadUnsignedByte = readUnsignedByte2 & 7;
            if (localPlayer.pathX[0] >= localX2 - scalar
               && localPlayer.pathX[0] <= localX2 + scalar
               && localPlayer.pathY[0] >= localY2 - scalar
               && localPlayer.pathY[0] <= localY2 + scalar
               && soundEffectVolume != 0
               && soundTypeOrReadUnsignedByte > 0
               && this.currentSound < 50) {
               this.sound[this.currentSound] = soundOrReadUnsignedShort;
               this.soundType[this.currentSound] = soundTypeOrReadUnsignedByte;
               this.soundVolume[this.currentSound] = 0;
               queuedSoundEffects[this.currentSound] = null;
               this.currentSound++;
            }
         }

         if (pktType == 215) {
            int idOrReadUnsignedShortAdded = newBuffer.readUnsignedShortAdded();
            int decodedUnsignedByteSubtracted = newBuffer.readUnsignedByteSubtracted();
            int position = this.localX + (decodedUnsignedByteSubtracted >> 4 & 7);
            int position2 = this.localY + (decodedUnsignedByteSubtracted & 7);
            int decodedUnsignedShortAdded = newBuffer.readUnsignedShortAdded();
            int sourceQuantity = newBuffer.readUnsignedShort();
            if (position >= 0 && position2 >= 0 && position < 104 && position2 < 104 && decodedUnsignedShortAdded != this.localPlayerIndex) {
               GroundItem node;
               (node = new GroundItem()).id = idOrReadUnsignedShortAdded;
               node.quantity = sourceQuantity;
               if (this.groundItems[this.plane][position][position2] == null) {
                  this.groundItems[this.plane][position][position2] = new NodeDeque();
               }

               this.groundItems[this.plane][position][position2].addLast(node);
               this.spawnGroundItem(position, position2);
               return;
            }
         } else if (pktType == 156) {
            int decodedUnsignedByteAdded = newBuffer.readUnsignedByteAdded();
            int localX3 = this.localX + (decodedUnsignedByteAdded >> 4 & 7);
            int localY3 = this.localY + (decodedUnsignedByteAdded & 7);
            int readUnsignedShort3 = newBuffer.readUnsignedShort();
            NodeDeque nodeDeque2;
            if (localX3 >= 0 && localY3 >= 0 && localX3 < 104 && localY3 < 104 && (nodeDeque2 = this.groundItems[this.plane][localX3][localY3]) != null) {
               for (GroundItem groundItem2 = (GroundItem)nodeDeque2.first(); groundItem2 != null; groundItem2 = (GroundItem)nodeDeque2.next()) {
                  if (groundItem2.id == (readUnsignedShort3 & 32767)) {
                     groundItem2.unlink();
                     break;
                  }
               }

               if (nodeDeque2.first() == null) {
                  this.groundItems[this.plane][localX3][localY3] = null;
               }

               this.spawnGroundItem(localX3, localY3);
               return;
            }
         } else if (pktType == 160) {
            int readUnsignedByteSubtracted2 = newBuffer.readUnsignedByteSubtracted();
            int position3 = this.localX + (readUnsignedByteSubtracted2 >> 4 & 7);
            int position4 = this.localY + (readUnsignedByteSubtracted2 & 7);
            int readUnsignedByteSubtracted3;
            int objectTypeSceneGroupIndex = (readUnsignedByteSubtracted3 = newBuffer.readUnsignedByteSubtracted()) >> 2;
            int scalar2 = readUnsignedByteSubtracted3 & 3;
            readUnsignedByteSubtracted2 = this.objectTypeSceneGroups[objectTypeSceneGroupIndex];
            int readUnsignedShortAdded2 = newBuffer.readUnsignedShortAdded();
            if (position3 >= 0 && position4 >= 0 && position3 < 103 && position4 < 103) {
               int localIntGroundArray = this.intGroundArray[this.plane][position3][position4];
               int intGroundArray2 = this.intGroundArray[this.plane][position3 + 1][position4];
               int intGroundArray3 = this.intGroundArray[this.plane][position3 + 1][position4 + 1];
               int intGroundArray4 = this.intGroundArray[this.plane][position3][position4 + 1];
               WallObject wallObject;
               if (readUnsignedByteSubtracted2 == 0 && (wallObject = this.scene.getWall(this.plane, position3, position4)) != null) {
                  int localHash = wallObject.hash >> 14 & 32767;
                  if (objectTypeSceneGroupIndex == 2) {
                     wallObject.primary = new DynamicObject(localHash, scalar2 + 4, 2, intGroundArray2, intGroundArray3, localIntGroundArray, intGroundArray4, readUnsignedShortAdded2, false);
                     wallObject.secondary = new DynamicObject(localHash, scalar2 + 1 & 3, 2, intGroundArray2, intGroundArray3, localIntGroundArray, intGroundArray4, readUnsignedShortAdded2, false);
                  } else {
                     wallObject.primary = new DynamicObject(localHash, scalar2, objectTypeSceneGroupIndex, intGroundArray2, intGroundArray3, localIntGroundArray, intGroundArray4, readUnsignedShortAdded2, false);
                  }
               }

               GroundDecoration groundDecoration;
               if (readUnsignedByteSubtracted2 == 1 && (groundDecoration = this.scene.getWallDecoration(position3, position4, this.plane)) != null) {
                  groundDecoration.renderable = new DynamicObject(groundDecoration.hash >> 14 & 32767, 0, 4, intGroundArray2, intGroundArray3, localIntGroundArray, intGroundArray4, readUnsignedShortAdded2, false);
               }

               if (readUnsignedByteSubtracted2 == 2) {
                  InteractiveObject interactiveObject = this.scene.getInteractiveObject(position3, position4, this.plane);
                  if (objectTypeSceneGroupIndex == 11) {
                     objectTypeSceneGroupIndex = 10;
                  }

                  if (interactiveObject != null) {
                     interactiveObject.renderable = new DynamicObject(interactiveObject.hash >> 14 & 32767, scalar2, objectTypeSceneGroupIndex, intGroundArray2, intGroundArray3, localIntGroundArray, intGroundArray4, readUnsignedShortAdded2, false);
                  }
               }

               WallDecoration wallDecoration;
               if (readUnsignedByteSubtracted2 == 3 && (wallDecoration = this.scene.getFloorDecoration(position4, position3, this.plane)) != null) {
                  wallDecoration.renderable = new DynamicObject(wallDecoration.hash >> 14 & 32767, scalar2, 22, intGroundArray2, intGroundArray3, localIntGroundArray, intGroundArray4, readUnsignedShortAdded2, false);
                  return;
               }
            }
         } else {
            if (pktType == 147) {
               int readUnsignedByteSubtracted4 = newBuffer.readUnsignedByteSubtracted();
               int position5 = this.localX + (readUnsignedByteSubtracted4 >> 4 & 7);
               int position6 = this.localY + (readUnsignedByteSubtracted4 & 7);
               int readUnsignedShort4 = newBuffer.readUnsignedShort();
               Buffer buffer = newBuffer;
               byte sourceDecodedByte = (byte)(128 - buffer.buffer[buffer.currentPosition++]);
               int decodedUnsignedShortLittleEndian2 = newBuffer.readUnsignedShortLittleEndian();
               byte decodedByteNegated = newBuffer.readByteNegated();
               int attachedModelEndCycleOrReadUnsignedShort = newBuffer.readUnsignedShort();
               int readUnsignedByteSubtracted5;
               int objectTypeSceneGroupIndex2 = (readUnsignedByteSubtracted5 = newBuffer.readUnsignedByteSubtracted()) >> 2;
               int wallFlagIndex = readUnsignedByteSubtracted5 & 3;
               int objectTypeSceneGroup = this.objectTypeSceneGroups[objectTypeSceneGroupIndex2];
               byte decodedByte = newBuffer.readByte();
               int readUnsignedShort5 = newBuffer.readUnsignedShort();
               byte readByteNegated2 = newBuffer.readByteNegated();
               Player player;
               if (readUnsignedShort4 == this.localPlayerIndex) {
                  player = localPlayer;
               } else {
                  player = this.players[readUnsignedShort4];
               }

               if (player != null) {
                  ObjectDefinition objectDefinition = ObjectDefinition.lookup(readUnsignedShort5);
                  int intGroundArray5 = this.intGroundArray[this.plane][position5][position6];
                  int intGroundArray6 = this.intGroundArray[this.plane][position5 + 1][position6];
                  int intGroundArray7 = this.intGroundArray[this.plane][position5 + 1][position6 + 1];
                  int intGroundArray8 = this.intGroundArray[this.plane][position5][position6 + 1];
                  Model model;
                  if ((model = objectDefinition.modelAt(objectTypeSceneGroupIndex2, wallFlagIndex, intGroundArray5, intGroundArray6, intGroundArray7, intGroundArray8, -1, -1, -1, -1)) != null) {
                     this.scheduleSpawnedObject(attachedModelEndCycleOrReadUnsignedShort + 1, -1, 0, objectTypeSceneGroup, position6, 0, this.plane, position5, decodedUnsignedShortLittleEndian2 + 1);
                     player.attachedModelStartCycle = decodedUnsignedShortLittleEndian2 + gameCycle;
                     player.attachedModelEndCycle = attachedModelEndCycleOrReadUnsignedShort + gameCycle;
                     player.attachedModel = model;
                     decodedUnsignedShortLittleEndian2 = objectDefinition.sizeX;
                     attachedModelEndCycleOrReadUnsignedShort = objectDefinition.sizeY;
                     if (wallFlagIndex == 1 || wallFlagIndex == 3) {
                        decodedUnsignedShortLittleEndian2 = objectDefinition.sizeY;
                        attachedModelEndCycleOrReadUnsignedShort = objectDefinition.sizeX;
                     }

                     player.attachedModelX = (position5 << 7) + (decodedUnsignedShortLittleEndian2 << 6);
                     player.attachedModelY = (position6 << 7) + (attachedModelEndCycleOrReadUnsignedShort << 6);
                     player.attachedModelHeight = this.getTileHeight(this.plane, player.attachedModelY, player.attachedModelX);
                     if (decodedByte > sourceDecodedByte) {
                        byte sourceDecodedByte2 = decodedByte;
                        decodedByte = sourceDecodedByte;
                        sourceDecodedByte = sourceDecodedByte2;
                     }

                     if (readByteNegated2 > decodedByteNegated) {
                        byte sourceDecodedByteNegated = readByteNegated2;
                        readByteNegated2 = decodedByteNegated;
                        decodedByteNegated = sourceDecodedByteNegated;
                     }

                     player.attachedModelMinX = position5 + decodedByte;
                     player.attachedModelMaxX = position5 + sourceDecodedByte;
                     player.attachedModelMinY = position6 + readByteNegated2;
                     player.attachedModelMaxY = position6 + decodedByteNegated;
                  }
               }
            }

            if (pktType == 151) {
               int readUnsignedByteAdded2 = newBuffer.readUnsignedByteAdded();
               int localX4 = this.localX + (readUnsignedByteAdded2 >> 4 & 7);
               int localY4 = this.localY + (readUnsignedByteAdded2 & 7);
               int decodedUnsignedShortLittleEndian = newBuffer.readUnsignedShortLittleEndian();
               int readUnsignedByteSubtracted6 = newBuffer.readUnsignedByteSubtracted();
               ObjectDefinition objectDefinition2;
               if ((objectDefinition2 = ObjectDefinition.lookup(decodedUnsignedShortLittleEndian)).animationId != -1) {
                  readUnsignedByteAdded2 = getAnimationFrameArchiveId(objectDefinition2.animationId);
                  if ((hdModels || !hdModels && extendedRevisionEnabled && (readUnsignedByteAdded2 > 1043 || use2007Models || extendedModelIds.contains(readUnsignedByteAdded2))) && readUnsignedByteAdded2 != -1) {
                     try {
                        if (AnimationFrame.frameCache.get(readUnsignedByteAdded2) == null) {
                           this.onDemandFetcher.provide(1, readUnsignedByteAdded2);
                        }
                     } catch (Exception exception) {
                     }
                  }
               }

               int objectTypeSceneGroupIndex3 = readUnsignedByteSubtracted6 >> 2;
               readUnsignedByteAdded2 = readUnsignedByteSubtracted6 & 3;
               int objectTypeSceneGroups2 = this.objectTypeSceneGroups[objectTypeSceneGroupIndex3];
               this.baseX = this.mapRegionX - 6 << 3;
               this.baseY = this.mapRegionY - 6 << 3;
               if (localX4 >= 0 && localY4 >= 0 && localX4 < 104 && localY4 < 104) {
                  this.scheduleSpawnedObject(-1, decodedUnsignedShortLittleEndian, readUnsignedByteAdded2, objectTypeSceneGroups2, localY4, objectTypeSceneGroupIndex3, this.plane, localX4, 0);
                  return;
               }
            } else if (pktType == 4) {
               int readUnsignedByte3 = newBuffer.readUnsignedByte();
               int localX5 = this.localX + (readUnsignedByte3 >> 4 & 7);
               int localY5 = this.localY + (readUnsignedByte3 & 7);
               int readUnsignedShort6 = newBuffer.readUnsignedShort();
               int readUnsignedByte4 = newBuffer.readUnsignedByte();
               int readUnsignedShort7 = newBuffer.readUnsignedShort();
               if (localX5 >= 0 && localY5 >= 0 && localX5 < 104 && localY5 < 104) {
                  localX5 = (localX5 << 7) + 64;
                  localY5 = (localY5 << 7) + 64;
                  SpotAnimation spotAnimation = new SpotAnimation(
                     this.plane, gameCycle, readUnsignedShort7, readUnsignedShort6, this.getTileHeight(this.plane, localY5, localX5) - readUnsignedByte4, localY5, localX5
                  );
                  this.incompleteAnimables.addLast(spotAnimation);
                  return;
               }
            } else if (pktType == 44) {
               int idOrReadUnsignedShortLittleEndianAdded = newBuffer.readUnsignedShortLittleEndianAdded();
               int quantityOrReadInt = newBuffer.readInt();
               int readUnsignedByte5 = newBuffer.readUnsignedByte();
               int position7 = this.localX + (readUnsignedByte5 >> 4 & 7);
               int position8 = this.localY + (readUnsignedByte5 & 7);
               if (position7 >= 0 && position8 >= 0 && position7 < 104 && position8 < 104) {
                  GroundItem groundItem3;
                  (groundItem3 = new GroundItem()).id = idOrReadUnsignedShortLittleEndianAdded;
                  groundItem3.quantity = quantityOrReadInt;
                  if (this.groundItems[this.plane][position7][position8] == null) {
                     this.groundItems[this.plane][position7][position8] = new NodeDeque();
                  }

                  this.groundItems[this.plane][position7][position8].addLast(groundItem3);
                  this.spawnGroundItem(position7, position8);
                  return;
               }
            } else if (pktType == 101) {
               int decodedUnsignedByteNegated;
               int objectTypeSceneGroupIndex4 = (decodedUnsignedByteNegated = newBuffer.readUnsignedByteNegated()) >> 2;
               int scalar3 = decodedUnsignedByteNegated & 3;
               int objectTypeSceneGroups3 = this.objectTypeSceneGroups[objectTypeSceneGroupIndex4];
               int readUnsignedByte6 = newBuffer.readUnsignedByte();
               int localX6 = this.localX + (readUnsignedByte6 >> 4 & 7);
               decodedUnsignedByteNegated = this.localY + (readUnsignedByte6 & 7);
               if (localX6 >= 0 && decodedUnsignedByteNegated >= 0 && localX6 < 104 && decodedUnsignedByteNegated < 104) {
                  this.scheduleSpawnedObject(-1, -1, scalar3, objectTypeSceneGroups3, decodedUnsignedByteNegated, objectTypeSceneGroupIndex4, this.plane, localX6, 0);
                  return;
               }
            } else if (pktType == 117) {
               int readUnsignedByte7 = newBuffer.readUnsignedByte();
               int localX7 = this.localX + (readUnsignedByte7 >> 4 & 7);
               int localY6 = this.localY + (readUnsignedByte7 & 7);
               int worldX = localX7 + newBuffer.readByte();
               int worldY = localY6 + newBuffer.readByte();
               int decodedShort = newBuffer.readShort();
               readUnsignedByte7 = newBuffer.readUnsignedShort();
               int readUnsignedByte8 = newBuffer.readUnsignedByte() << 2;
               int readUnsignedByte9 = newBuffer.readUnsignedByte() << 2;
               int readUnsignedShort8 = newBuffer.readUnsignedShort();
               int readUnsignedShort9 = newBuffer.readUnsignedShort();
               int readUnsignedByte10 = newBuffer.readUnsignedByte();
               int readUnsignedByte11 = newBuffer.readUnsignedByte();
               if (localX7 >= 0 && localY6 >= 0 && localX7 < 104 && localY6 < 104 && worldX >= 0 && worldY >= 0 && worldX < 104 && worldY < 104 && readUnsignedByte7 != 65535) {
                  localX7 = (localX7 << 7) + 64;
                  localY6 = (localY6 << 7) + 64;
                  worldX = (worldX << 7) + 64;
                  worldY = (worldY << 7) + 64;
                  Projectile projectile;
                  (projectile = new Projectile(
                        readUnsignedByte10,
                        readUnsignedByte9,
                        readUnsignedShort8 + gameCycle,
                        readUnsignedShort9 + gameCycle,
                        readUnsignedByte11,
                        this.plane,
                        this.getTileHeight(this.plane, localY6, localX7) - readUnsignedByte8,
                        localY6,
                        localX7,
                        decodedShort,
                        readUnsignedByte7
                     ))
                     .trackTarget(readUnsignedShort8 + gameCycle, worldY, this.getTileHeight(this.plane, worldY, worldX) - readUnsignedByte9, worldX);
                  this.projectiles.addLast(projectile);
               }
            }
         }
      }
   }
   private void drawMinimapIcon(Sprite sprite, int newScreenMode, int screenMode2) {
      if (sprite != null) {
         if (screenMode != 0) {
            int sINEIndex = this.minimapInt1 + this.minimapInt2 & 2047;
            int scalar;
            if ((scalar = newScreenMode * newScreenMode + screenMode2 * screenMode2) <= 6400) {
               int sINEEntry = Model.SINE[sINEIndex];
               int cOSINEEntry = Model.COSINE[sINEIndex];
               sINEEntry = (sINEEntry << 8) / (this.minimapInt3 + 256);
               cOSINEEntry = (cOSINEEntry << 8) / (this.minimapInt3 + 256);
               sINEIndex = screenMode2 * sINEEntry + newScreenMode * cOSINEEntry >> 16;
               sINEEntry = screenMode2 * cOSINEEntry - newScreenMode * sINEEntry >> 16;
               newScreenMode = screenMode == 0 ? sINEIndex + 94 - sprite.canvasWidth / 2 + 4 : sINEIndex + 94 - sprite.canvasWidth / 2 + 4 + clientWidth - 207;
               screenMode2 = screenMode == 0 ? 83 - sINEEntry - sprite.canvasHeight / 2 - 4 : 83 - sINEEntry - sprite.canvasHeight / 2 - 4 + 9;
               if (scalar > 5000) {
                  sprite.drawMaskedMinimapBufferSprite(mapBack, screenMode2, newScreenMode);
                  return;
               }

               sprite.drawSprite(newScreenMode, screenMode2);
            }
         } else {
            if (gameframeVersion != 474 || gameframeVersion == 474 && !orbsEnabled) {
               int minimapInt12 = this.minimapInt1 + this.minimapInt2 & 2047;
               int scalar2;
               if ((scalar2 = newScreenMode * newScreenMode + screenMode2 * screenMode2) <= 6400) {
                  int SINE2 = Model.SINE[minimapInt12];
                  int COSINE2 = Model.COSINE[minimapInt12];
                  SINE2 = (SINE2 << 8) / (this.minimapInt3 + 256);
                  COSINE2 = (COSINE2 << 8) / (this.minimapInt3 + 256);
                  minimapInt12 = screenMode2 * SINE2 + newScreenMode * COSINE2 >> 16;
                  SINE2 = screenMode2 * COSINE2 - newScreenMode * SINE2 >> 16;
                  if (scalar2 > 2500) {
                     sprite.drawMaskedSprite(mapBack, 83 - SINE2 - sprite.canvasHeight / 2 - 4, minimapInt12 + 94 - sprite.canvasWidth / 2 + 4);
                     return;
                  }

                  sprite.drawSprite(minimapInt12 + 94 - sprite.canvasWidth / 2 + 4, 83 - SINE2 - sprite.canvasHeight / 2 - 4);
                  return;
               }
            } else {
               int minimapInt13 = this.minimapInt1 + this.minimapInt2 & 2047;
               int scalar3;
               if ((scalar3 = newScreenMode * newScreenMode + screenMode2 * screenMode2) <= 6400) {
                  int SINE3 = Model.SINE[minimapInt13];
                  minimapInt13 = Model.COSINE[minimapInt13];
                  SINE3 = (SINE3 << 8) / (this.minimapInt3 + 256);
                  minimapInt13 = (minimapInt13 << 8) / (this.minimapInt3 + 256);
                  int scalar4 = screenMode2 * SINE3 + newScreenMode * minimapInt13 >> 16;
                  newScreenMode = screenMode2 * minimapInt13 - newScreenMode * SINE3 >> 16;
                  if (scalar3 > 2500) {
                     sprite.drawMappedMaskedMinimapSprite(mapBack, 83 - newScreenMode - sprite.canvasHeight / 2 - 4, scalar4 + 94 - sprite.canvasWidth / 2 + 4);
                     return;
                  }

                  sprite.drawSprite(scalar4 + 94 - sprite.canvasWidth / 2 + 4 + 29, 83 - newScreenMode - sprite.canvasHeight / 2 - 4);
               }
            }
         }
      }
   }
   private void updateWorldObject(int tileY, int plane, int orientation, int objectType, int tileX, int objectCategory, int objectId) {
      if (tileX > 0 && tileY > 0 && tileX <= 102 && tileY <= 102) {
         int localScene = 0;
         if (objectCategory == 0) {
            localScene = this.scene.getWallHash(plane, tileX, tileY);
         }

         if (objectCategory == 1) {
            localScene = this.scene.getWallDecorationHash(plane, tileX, tileY);
         }

         if (objectCategory == 2) {
            localScene = this.scene.getInteractiveObjectHash(plane, tileX, tileY);
         }

         if (objectCategory == 3) {
            localScene = this.scene.getFloorDecorationHash(plane, tileX, tileY);
         }

         if (localScene != 0) {
            int scene2 = this.scene.getArrangement(plane, tileX, tileY, localScene);
            localScene = localScene >> 14 & 32767;
            int scalar = scene2 & 31;
            scene2 >>= 6;
            if (objectCategory == 0) {
               this.scene.removeWall(tileX, plane, tileY, (byte)-119);
               ObjectDefinition objectDefinition;
               if ((objectDefinition = ObjectDefinition.lookup(localScene)).solid) {
                  this.collisionMaps[plane].removeWall(scene2, scalar, objectDefinition.walkable, tileX, tileY);
               }
            }

            if (objectCategory == 1) {
               this.scene.removeWallDecoration(tileY, plane, tileX);
            }

            if (objectCategory == 2) {
               this.scene.removeInteractiveObject(plane, tileX, tileY);
               ObjectDefinition objectDefinition3 = ObjectDefinition.lookup(localScene);
               if (tileX + objectDefinition3.sizeX > 103 || tileY + objectDefinition3.sizeX > 103 || tileX + objectDefinition3.sizeY > 103 || tileY + objectDefinition3.sizeY > 103) {
                  return;
               }

               if (objectDefinition3.solid) {
                  this.collisionMaps[plane].removeObject(scene2, objectDefinition3.sizeX, tileX, tileY, objectDefinition3.sizeY, objectDefinition3.walkable);
               }
            }

            if (objectCategory == 3) {
               this.scene.removeFloorDecoration(plane, tileY, tileX);
               ObjectDefinition objectDefinition2;
               if ((objectDefinition2 = ObjectDefinition.lookup(localScene)).solid && objectDefinition2.hasActions) {
                  CollisionMap collisionMap = this.collisionMaps[plane];
                  scene2 = tileX;
                  localScene = tileY;
                  CollisionMap sourceCollisionMap = collisionMap;
                  sourceCollisionMap.clippingData[scene2][localScene] = sourceCollisionMap.clippingData[scene2][localScene] & 14680063;
               }
            }
         }

         if (objectId >= 0) {
            int sourceTileIndex = plane;
            if (plane < 3 && (this.byteGroundArray[1][tileX][tileY] & 2) == 2) {
               sourceTileIndex = plane + 1;
            }

            RegionBuilder.addObjectStatic(this.scene, orientation, tileY, objectType, sourceTileIndex, this.collisionMaps[plane], this.intGroundArray, tileX, objectId, plane);
         }
      }
   }
   private void updatePlayers(int packetSize, Buffer buffer) {
      this.removedEntityCount = 0;
      this.entityUpdateCount = 0;
      Buffer sourceBuffer = buffer;
      Client client = this;
      sourceBuffer.startBitAccess();
      if (sourceBuffer.readBits(1) != 0) {
         int decodedBits;
         if ((decodedBits = sourceBuffer.readBits(2)) == 0) {
            client.entityUpdateIndices[client.entityUpdateCount++] = 2047;
         } else if (decodedBits == 1) {
            int readBits2 = sourceBuffer.readBits(3);
            localPlayer.moveInDirection(false, readBits2);
            if (sourceBuffer.readBits(1) == 1) {
               client.entityUpdateIndices[client.entityUpdateCount++] = 2047;
            }
         } else if (decodedBits == 2) {
            int readBits3 = sourceBuffer.readBits(3);
            localPlayer.moveInDirection(true, readBits3);
            int readBits4 = sourceBuffer.readBits(3);
            localPlayer.moveInDirection(true, readBits4);
            if (sourceBuffer.readBits(1) == 1) {
               client.entityUpdateIndices[client.entityUpdateCount++] = 2047;
            }
         } else if (decodedBits == 3) {
            client.plane = sourceBuffer.readBits(2);
            int readBits5 = sourceBuffer.readBits(1);
            if (sourceBuffer.readBits(1) == 1) {
               client.entityUpdateIndices[client.entityUpdateCount++] = 2047;
            }

            int readBits6 = sourceBuffer.readBits(7);
            int readBits7 = sourceBuffer.readBits(7);
            localPlayer.setPosition(readBits7, readBits6, readBits5 == 1);
         }
      }

      sourceBuffer = buffer;
      client = this;
      int decodedBits2;
      if ((decodedBits2 = sourceBuffer.readBits(8)) < client.playerCount) {
         for (int playerIndex = decodedBits2; playerIndex < client.playerCount; playerIndex++) {
            client.removedEntityIndices[client.removedEntityCount++] = client.playerIndices[playerIndex];
         }
      }

      if (decodedBits2 > client.playerCount) {
         SignLink.reporterror(username + " Too many players");
         throw new RuntimeException("eek");
      }

      client.playerCount = 0;

      for (int playerIndex2 = 0; playerIndex2 < decodedBits2; playerIndex2++) {
         int playerIndex3 = client.playerIndices[playerIndex2];
         Player player;
         (player = client.players[playerIndex3]).index = playerIndex3;
         if (sourceBuffer.readBits(1) == 0) {
            client.playerIndices[client.playerCount++] = playerIndex3;
            player.lastUpdateCycle = gameCycle;
         } else {
            int readBits8;
            if ((readBits8 = sourceBuffer.readBits(2)) == 0) {
               client.playerIndices[client.playerCount++] = playerIndex3;
               player.lastUpdateCycle = gameCycle;
               client.entityUpdateIndices[client.entityUpdateCount++] = playerIndex3;
            } else if (readBits8 == 1) {
               client.playerIndices[client.playerCount++] = playerIndex3;
               player.lastUpdateCycle = gameCycle;
               int readBits9 = sourceBuffer.readBits(3);
               player.moveInDirection(false, readBits9);
               if (sourceBuffer.readBits(1) == 1) {
                  client.entityUpdateIndices[client.entityUpdateCount++] = playerIndex3;
               }
            } else if (readBits8 == 2) {
               client.playerIndices[client.playerCount++] = playerIndex3;
               player.lastUpdateCycle = gameCycle;
               int readBits10 = sourceBuffer.readBits(3);
               player.moveInDirection(true, readBits10);
               readBits10 = sourceBuffer.readBits(3);
               player.moveInDirection(true, readBits10);
               if (sourceBuffer.readBits(1) == 1) {
                  client.entityUpdateIndices[client.entityUpdateCount++] = playerIndex3;
               }
            } else if (readBits8 == 3) {
               client.removedEntityIndices[client.removedEntityCount++] = playerIndex3;
            }
         }
      }

      this.updateOtherPlayerMovement(buffer, packetSize);
      this.parsePlayerUpdateMasks(buffer, packetSize);

      for (int removedEntityIndex = 0; removedEntityIndex < this.removedEntityCount; removedEntityIndex++) {
         int removedEntityIndex2 = this.removedEntityIndices[removedEntityIndex];
         if (this.players[removedEntityIndex2].lastUpdateCycle != gameCycle) {
            this.players[removedEntityIndex2] = null;
         }
      }

      if (buffer.currentPosition != packetSize) {
         SignLink.reporterror("Error packet size mismatch in getplayer pos:" + buffer.currentPosition + " psize:" + packetSize);
         throw new RuntimeException("eek");
      }

      for (int playerIndex4 = 0; playerIndex4 < this.playerCount; playerIndex4++) {
         if (this.players[this.playerIndices[playerIndex4]] == null) {
            SignLink.reporterror(username + " null entry in pl list - pos:" + playerIndex4 + " size:" + this.playerCount);
            throw new RuntimeException("eek");
         }
      }
   }
   private void setCameraPos(int scalarArgument, int newZCameraPos, int newCameraPositionX, int newCameraPositionZ, int newYCameraPos, int newXCameraPos) {
      int sINEIndex = 2048 - newZCameraPos & 2047;
      int sINEIndex2 = 2048 - newYCameraPos & 2047;
      int scalar = 0;
      int scalar2 = 0;
      int scalar3 = scalarArgument;
      if (sINEIndex != 0) {
         int sINEEntry = Model.SINE[sINEIndex];
         sINEIndex = Model.COSINE[sINEIndex];
         int scalar4 = 0 * sINEIndex - scalarArgument * sINEEntry >> 16;
         scalar3 = 0 * sINEEntry + scalarArgument * sINEIndex >> 16;
         scalar2 = scalar4;
      }

      if (sINEIndex2 != 0) {
         int SINE2 = Model.SINE[sINEIndex2];
         sINEIndex = Model.COSINE[sINEIndex2];
         int scalar5 = scalar3 * SINE2 + 0 * sINEIndex >> 16;
         scalar3 = scalar3 * sINEIndex - 0 * SINE2 >> 16;
         scalar = scalar5;
      }

      this.cameraPositionX = newCameraPositionX - scalar;
      this.cameraPositionZ = newCameraPositionZ - scalar2;
      this.xCameraPos = newXCameraPos - scalar3;
      this.zCameraPos = newZCameraPos;
      this.yCameraPos = newYCameraPos;
   }
   private boolean parsePacket() {
      if (this.connection == null) {
         return false;
      }

      try {
         int widgetIndex2;
         if ((widgetIndex2 = this.connection.available()) == 0) {
            return false;
         }

         if (this.pktType == -1) {
            this.connection.flushInputStream(this.inStream.buffer, 1);
            this.pktType = this.inStream.buffer[0] & 255;
            if (this.incomingIsaacCipher != null) {
               this.pktType = this.pktType - this.incomingIsaacCipher.nextInt() & 0xFF;
            }

            this.pktSize = IncomingPacketLengths.lengths[this.pktType];
            widgetIndex2--;
         }

         if (this.pktSize == -1) {
            if (widgetIndex2 <= 0) {
               return false;
            }

            this.connection.flushInputStream(this.inStream.buffer, 1);
            this.pktSize = this.inStream.buffer[0] & 255;
            widgetIndex2--;
         }

         if (this.pktSize == -2) {
            if (widgetIndex2 <= 1) {
               return false;
            }

            this.connection.flushInputStream(this.inStream.buffer, 2);
            this.inStream.currentPosition = 0;
            this.pktSize = this.inStream.readUnsignedShort();
            widgetIndex2 -= 2;
         }

         if (widgetIndex2 < this.pktSize) {
            return false;
         }

         if (this.pktSize > this.inStream.buffer.length) {
            int newCapacity = Math.max(1, this.inStream.buffer.length);
            while (newCapacity < this.pktSize) {
               int doubled = newCapacity << 1;
               if (doubled <= newCapacity) {
                  newCapacity = this.pktSize;
                  break;
               }
               newCapacity = doubled;
            }
            this.inStream.buffer = new byte[newCapacity];
         }

         this.inStream.currentPosition = 0;
         this.connection.flushInputStream(this.inStream.buffer, this.pktSize);
         this.timeoutCounter = 0;
         this.prevPktType2 = this.prevPktType;
         this.prevPktType = this.lastOpcode;
         this.lastOpcode = this.pktType;
         if (this.pktType == 81) {
            this.updatePlayers(this.pktSize, this.inStream);
            this.validLocalMap = false;
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 176) {
            this.daysSinceRecovChange = this.inStream.readUnsignedShort();
            this.unreadMessages = this.inStream.readUnsignedShortAdded();
            this.membersInt = this.inStream.readUnsignedByte();
            this.lastLoginIp = this.inStream.readIntInverseMiddleEndian();
            this.lastLoginDaysAgo = this.inStream.readUnsignedShort();
            this.daysSinceLastLogin = this.inStream.readUnsignedShort();
            int openInterfaceIdOrInStream = this.inStream.readUnsignedShort();
            if (this.lastLoginIp != 0 && this.openInterfaceId == -1) {
               SignLink.dnslookup(NameUtils.formatIpv4Address(this.lastLoginIp));
               this.closeTopInterfaces();
               this.reportAbuseInput = "";
               this.canMute = false;
               this.openInterfaceId = openInterfaceIdOrInStream;
               this.fullscreenInterfaceId = 15244;
            }

            this.pktType = -1;
            return true;
         }

         if (this.pktType == 64) {
            this.localX = this.inStream.readUnsignedByteNegated();
            this.localY = this.inStream.readUnsignedByteSubtracted();

            for (int localX = this.localX; localX < this.localX + 8; localX++) {
               for (int localY = this.localY; localY < this.localY + 8; localY++) {
                  if (this.groundItems[this.plane][localX][localY] != null) {
                     this.groundItems[this.plane][localX][localY] = null;
                     this.spawnGroundItem(localX, localY);
                  }
               }
            }

            for (SpawnedObject spawnedObject = (SpawnedObject)this.spawns.first(); spawnedObject != null; spawnedObject = (SpawnedObject)this.spawns.next()) {
               if (spawnedObject.x >= this.localX
                  && spawnedObject.x < this.localX + 8
                  && spawnedObject.y >= this.localY
                  && spawnedObject.y < this.localY + 8
                  && spawnedObject.plane == this.plane) {
                  spawnedObject.longevity = 0;
               }
            }

            this.pktType = -1;
            return true;
         }

         if (this.pktType == 185) {
            int widgetIndex = this.inStream.readUnsignedShortLittleEndianAdded();
            Widget.widgets[widgetIndex].defaultMediaType = 3;
            if (localPlayer.npcDefinition == null) {
               Widget.widgets[widgetIndex].defaultMediaId = (localPlayer.appearanceColors[0] << 25)
                  + (localPlayer.appearanceColors[4] << 20)
                  + (localPlayer.equipment[0] << 15)
                  + (localPlayer.equipment[8] << 10)
                  + (localPlayer.equipment[11] << 5)
                  + localPlayer.equipment[1];
            } else {
               Widget.widgets[widgetIndex].defaultMediaId = (int)(305419896L + localPlayer.npcDefinition.id);
            }

            this.pktType = -1;
            return true;
         }

         if (this.pktType == 107) {
            this.oriented = false;

            for (int cameraShakeActiveIndex = 0; cameraShakeActiveIndex < 5; cameraShakeActiveIndex++) {
               this.cameraShakeActive[cameraShakeActiveIndex] = false;
            }

            this.pktType = -1;
            return true;
         }

         if (this.pktType == 72) {
            int decodedUnsignedShortLittleEndian = this.inStream.readUnsignedShortLittleEndian();
            Widget widget = Widget.widgets[decodedUnsignedShortLittleEndian];

            for (int inventoryIdIndex = 0; inventoryIdIndex < widget.inventoryIds.length; inventoryIdIndex++) {
               widget.inventoryIds[inventoryIdIndex] = -1;
               widget.inventoryIds[inventoryIdIndex] = 0;
            }

            this.pktType = -1;
            return true;
         }

         if (this.pktType == 214) {
            this.ignoreCount = this.pktSize / 8;

            for (int ignoreListAsLongIndex = 0; ignoreListAsLongIndex < this.ignoreCount; ignoreListAsLongIndex++) {
               this.ignoreListAsLongs[ignoreListAsLongIndex] = this.inStream.readLong();
            }

            this.pktType = -1;
            return true;
         }

         if (this.pktType == 166) {
            this.oriented = true;
            this.x = this.inStream.readUnsignedByte();
            this.y = this.inStream.readUnsignedByte();
            this.height = this.inStream.readUnsignedShort();
            this.speed = this.inStream.readUnsignedByte();
            this.angle = this.inStream.readUnsignedByte();
            if (this.angle >= 100) {
               this.cameraPositionX = (this.x << 7) + 64;
               this.xCameraPos = (this.y << 7) + 64;
               this.cameraPositionZ = this.getTileHeight(this.plane, this.xCameraPos, this.cameraPositionX) - this.height;
            }

            this.pktType = -1;
            return true;
         }

         if (this.pktType == 134) {
            this.needDrawTabArea = true;
            int currentExpIndex = this.inStream.readUnsignedByte();
            int currentExpOrInStream = this.inStream.readIntMiddleEndian();
            widgetIndex2 = this.inStream.readUnsignedByte();
            if (this.customSettingVisualFixes) {
               int scalar = currentExpOrInStream - this.currentExp[currentExpIndex];
               addExperienceDrop(currentExpIndex, scalar);
            }

            this.currentExp[currentExpIndex] = currentExpOrInStream;
            this.currentStats[currentExpIndex] = widgetIndex2;
            this.maxStats[currentExpIndex] = 1;

            for (int experienceTableIndex = 0; experienceTableIndex < 98; experienceTableIndex++) {
               if (currentExpOrInStream >= experienceTable[experienceTableIndex]) {
                  this.maxStats[currentExpIndex] = experienceTableIndex + 2;
               }
            }

            if (!this.customSettingVisualFixes && currentExpIndex == 21) {
               this.customSettingVisualFixes = true;
            }

            this.pktType = -1;
            return true;
         }

         if (this.pktType == 71) {
            int tabInterfaceIdOrInStream = this.inStream.readUnsignedShort();
            int inStream2 = this.inStream.readUnsignedByteAdded();
            if (tabInterfaceIdOrInStream == 65535) {
               tabInterfaceIdOrInStream = -1;
            }

            this.tabInterfaceIds[inStream2] = tabInterfaceIdOrInStream;
            this.needDrawTabArea = true;
            this.tabAreaAltered = true;
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 74) {
            int currentSongOrInStream;
            if ((currentSongOrInStream = this.inStream.readUnsignedShortLittleEndian()) == 65535) {
               currentSongOrInStream = -1;
            }

            if (currentSongOrInStream == -1 && this.previousSong == 0) {
               stopMidi(false);
            } else if (currentSongOrInStream != -1 && this.currentSong != currentSongOrInStream && musicVolumeSetting != 0 && this.previousSong == 0) {
               this.requestMusicTrackWithFade(18, musicVolumeSetting, currentSongOrInStream);
            }

            this.currentSong = currentSongOrInStream;
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 121) {
            int inStream3 = this.inStream.readUnsignedShortLittleEndianAdded();
            int previousSongOrInStream = this.inStream.readUnsignedShortAdded();
            if (inStream3 == 65535) {
               inStream3 = -1;
            }

            if (musicVolumeSetting != 0 && previousSongOrInStream != -1) {
               this.requestMusicTrackImmediate(musicVolumeSetting, inStream3);
               this.previousSong = previousSongOrInStream * 20;
            }

            this.pktType = -1;
            return true;
         }

         if (this.pktType == 109) {
            this.resetLogout();
            this.pktType = -1;
            return false;
         }

         if (this.pktType == 70) {
            int runtimeXOffsetOrInStream = this.inStream.readShort();
            int runtimeYOffsetOrInStream = this.inStream.readShortLittleEndian();
            widgetIndex2 = this.inStream.readUnsignedShortLittleEndian();
            Widget widget10;
            (widget10 = Widget.widgets[widgetIndex2]).runtimeXOffset = runtimeXOffsetOrInStream;
            widget10.runtimeYOffset = runtimeYOffsetOrInStream;
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 73 || this.pktType == 241) {
            widgetIndex2 = this.mapRegionX;
            int mapRegionY = this.mapRegionY;
            if (this.pktType == 73) {
               widgetIndex2 = this.inStream.readUnsignedShortAdded();
               mapRegionY = this.inStream.readUnsignedShort();
               this.constructedViewport = false;
            }

            if (this.pktType == 241) {
               mapRegionY = this.inStream.readUnsignedShortAdded();
               this.inStream.startBitAccess();

               for (int instanceChunkTemplateIndex = 0; instanceChunkTemplateIndex < 4; instanceChunkTemplateIndex++) {
                  for (int loopIndex = 0; loopIndex < 13; loopIndex++) {
                     for (int loopIndex2 = 0; loopIndex2 < 13; loopIndex2++) {
                        if (this.inStream.readBits(1) == 1) {
                           this.instanceChunkTemplates[instanceChunkTemplateIndex][loopIndex][loopIndex2] = this.inStream.readBits(26);
                        } else {
                           this.instanceChunkTemplates[instanceChunkTemplateIndex][loopIndex][loopIndex2] = -1;
                        }
                     }
                  }
               }

               this.inStream.finishBitAccess();
               widgetIndex2 = this.inStream.readUnsignedShort();
               this.constructedViewport = true;
            }

            if (this.pktType != 241 && this.mapRegionX == widgetIndex2 && this.mapRegionY == mapRegionY && this.loadingStage == 2) {
               this.pktType = -1;
               return true;
            }

            this.mapRegionX = widgetIndex2;
            this.mapRegionY = mapRegionY;
            this.baseX = this.mapRegionX - 6 << 3;
            this.baseY = this.mapRegionY - 6 << 3;
            this.inPlayerOwnedHouse = (this.mapRegionX / 8 == 48 || this.mapRegionX / 8 == 49) && this.mapRegionY / 8 == 48;
            if (this.mapRegionX / 8 == 48 && this.mapRegionY / 8 == 148) {
               this.inPlayerOwnedHouse = true;
            }

            this.loadingStage = 1;
            this.lastRegionLoadActivityMillis = System.currentTimeMillis();
            this.gameScreenImageProducer.initDrawingArea();
            this.drawLoadingMessages(1, "Loading - please wait.", null);
            this.gameScreenImageProducer.drawToBuffer(screenMode == 0 ? 4 : 0, this.frameBuffer, screenMode == 0 ? 4 : 0);
            if (this.pktType == 73) {
               int regionIdIndex = 0;

               for (int loopIndex3 = (this.mapRegionX - 6) / 8; loopIndex3 <= (this.mapRegionX + 6) / 8; loopIndex3++) {
                  for (int loopIndex4 = (this.mapRegionY - 6) / 8; loopIndex4 <= (this.mapRegionY + 6) / 8; loopIndex4++) {
                     regionIdIndex++;
                  }
               }

               this.terrainRegionData = new byte[regionIdIndex][];
               this.objectRegionData = new byte[regionIdIndex][];
               this.regionIds = new int[regionIdIndex];
               this.terrainArchiveIds = new int[regionIdIndex];
               this.objectArchiveIds = new int[regionIdIndex];
               regionIdIndex = 0;

               for (int loopIndex5 = (this.mapRegionX - 6) / 8; loopIndex5 <= (this.mapRegionX + 6) / 8; loopIndex5++) {
                  for (int loopIndex6 = (this.mapRegionY - 6) / 8; loopIndex6 <= (this.mapRegionY + 6) / 8; loopIndex6++) {
                     this.regionIds[regionIdIndex] = (loopIndex5 << 8) + loopIndex6;
                     if (!this.inPlayerOwnedHouse || loopIndex6 != 49 && loopIndex6 != 149 && loopIndex6 != 147 && loopIndex5 != 50 && (loopIndex5 != 49 || loopIndex6 != 47)) {
                        int localTerrainArchiveIds;
                        if ((localTerrainArchiveIds = this.terrainArchiveIds[regionIdIndex] = this.onDemandFetcher.getMapFileId(0, loopIndex6, loopIndex5)) != -1) {
                           this.onDemandFetcher.provide(3, localTerrainArchiveIds);
                        }

                        int localObjectArchiveIds;
                        if ((localObjectArchiveIds = this.objectArchiveIds[regionIdIndex] = this.onDemandFetcher.getMapFileId(1, loopIndex6, loopIndex5)) != -1) {
                           this.onDemandFetcher.provide(3, localObjectArchiveIds);
                        }
                     } else {
                        this.terrainArchiveIds[regionIdIndex] = -1;
                        this.objectArchiveIds[regionIdIndex] = -1;
                     }

                     regionIdIndex++;
                  }
               }
            }

            if (this.pktType == 241) {
               int loopIndex7 = 0;
               int[] regionId = new int[676];

               for (int instanceChunkTemplateIndex2 = 0; instanceChunkTemplateIndex2 < 4; instanceChunkTemplateIndex2++) {
                  for (int loopIndex8 = 0; loopIndex8 < 13; loopIndex8++) {
                     for (int loopIndex9 = 0; loopIndex9 < 13; loopIndex9++) {
                        int localInstanceChunkTemplates;
                        if ((localInstanceChunkTemplates = this.instanceChunkTemplates[instanceChunkTemplateIndex2][loopIndex8][loopIndex9]) != -1) {
                           int scalar2 = localInstanceChunkTemplates >> 14 & 1023;
                           int scalar3 = localInstanceChunkTemplates >> 3 & 2047;
                           int scalar4 = (scalar2 / 8 << 8) + scalar3 / 8;

                           for (int regionIdIndex2 = 0; regionIdIndex2 < loopIndex7; regionIdIndex2++) {
                              if (regionId[regionIdIndex2] == scalar4) {
                                 scalar4 = -1;
                                 break;
                              }
                           }

                           if (scalar4 != -1) {
                              regionId[loopIndex7++] = scalar4;
                           }
                        }
                     }
                  }
               }

               this.terrainRegionData = new byte[loopIndex7][];
               this.objectRegionData = new byte[loopIndex7][];
               this.regionIds = new int[loopIndex7];
               this.terrainArchiveIds = new int[loopIndex7];
               this.objectArchiveIds = new int[loopIndex7];

               for (int regionIdIndex3 = 0; regionIdIndex3 < loopIndex7; regionIdIndex3++) {
                  int localRegionIds;
                  int scalar5 = (localRegionIds = this.regionIds[regionIdIndex3] = regionId[regionIdIndex3]) >> 8 & 0xFF;
                  int scalar6 = localRegionIds & 0xFF;
                  int terrainArchiveIds2;
                  if ((terrainArchiveIds2 = this.terrainArchiveIds[regionIdIndex3] = this.onDemandFetcher.getMapFileId(0, scalar6, scalar5)) != -1) {
                     this.onDemandFetcher.provide(3, terrainArchiveIds2);
                  }

                  int objectArchiveIds2;
                  if ((objectArchiveIds2 = this.objectArchiveIds[regionIdIndex3] = this.onDemandFetcher.getMapFileId(1, scalar6, scalar5)) != -1) {
                     this.onDemandFetcher.provide(3, objectArchiveIds2);
                  }
               }
            }

            widgetIndex2 = this.baseX - this.previousBaseX;
            int localBaseY = this.baseY - this.previousBaseY;
            this.previousBaseX = this.baseX;
            this.previousBaseY = this.baseY;

            for (int npcIndex = 0; npcIndex < 16384; npcIndex++) {
               Npc npc;
               if ((npc = this.npcs[npcIndex]) != null) {
                  for (int pathIndex = 0; pathIndex < 10; pathIndex++) {
                     npc.pathX[pathIndex] = npc.pathX[pathIndex] - widgetIndex2;
                     npc.pathY[pathIndex] = npc.pathY[pathIndex] - localBaseY;
                  }

                  npc.worldX -= widgetIndex2 << 7;
                  npc.worldY -= localBaseY << 7;
               }
            }

            for (int playerIndex = 0; playerIndex < 2048; playerIndex++) {
               Player player;
               if ((player = this.players[playerIndex]) != null) {
                  for (int pathXIndex = 0; pathXIndex < 10; pathXIndex++) {
                     player.pathX[pathXIndex] = player.pathX[pathXIndex] - widgetIndex2;
                     player.pathY[pathXIndex] = player.pathY[pathXIndex] - localBaseY;
                  }

                  player.worldX -= widgetIndex2 << 7;
                  player.worldY -= localBaseY << 7;
               }
            }

            this.validLocalMap = true;
            byte position = 0;
            byte loopIndex10 = 104;
            byte byteCode = 1;
            if (widgetIndex2 < 0) {
               position = 103;
               loopIndex10 = -1;
               byteCode = -1;
            }

            byte position2 = 0;
            byte loopIndex11 = 104;
            byte byteCode2 = 1;
            if (localBaseY < 0) {
               position2 = 103;
               loopIndex11 = -1;
               byteCode2 = -1;
            }

            for (byte loopIndex12 = position; loopIndex12 != loopIndex10; loopIndex12 += byteCode) {
               for (byte loopIndex13 = position2; loopIndex13 != loopIndex11; loopIndex13 += byteCode2) {
                  int scalar7 = loopIndex12 + widgetIndex2;
                  int scalar8 = loopIndex13 + localBaseY;

                  for (int groundItemIndex = 0; groundItemIndex < 4; groundItemIndex++) {
                     if (scalar7 >= 0 && scalar8 >= 0 && scalar7 < 104 && scalar8 < 104) {
                        this.groundItems[groundItemIndex][loopIndex12][loopIndex13] = this.groundItems[groundItemIndex][scalar7][scalar8];
                     } else {
                        this.groundItems[groundItemIndex][loopIndex12][loopIndex13] = null;
                     }
                  }
               }
            }

            for (SpawnedObject spawnedObject2 = (SpawnedObject)this.spawns.first(); spawnedObject2 != null; spawnedObject2 = (SpawnedObject)this.spawns.next()) {
               spawnedObject2.x -= widgetIndex2;
               spawnedObject2.y -= localBaseY;
               if (spawnedObject2.x < 0 || spawnedObject2.y < 0 || spawnedObject2.x >= 104 || spawnedObject2.y >= 104) {
                  spawnedObject2.unlink();
               }
            }

            if (this.destX != 0) {
               this.destX -= widgetIndex2;
               this.destY -= localBaseY;
            }

            this.oriented = false;
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 208) {
            int openWalkableInterfaceOrInStream;
            if ((openWalkableInterfaceOrInStream = this.inStream.readShortLittleEndian()) >= 0) {
               this.resetWidgetAnimation(openWalkableInterfaceOrInStream);
            }

            this.openWalkableInterface = openWalkableInterfaceOrInStream;
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 98) {
            this.inStream.readUnsignedByte();
            long inStream4 = this.inStream.readLong();
            int inStream5 = this.inStream.readUnsignedByte();
            int inStream6 = this.inStream.readUnsignedByte();
            int inStream7 = this.inStream.readUnsignedByte();
            String text = NameUtils.formatDisplayName(NameUtils.decodeBase37(inStream4));
            String decodedString = this.inStream.readString();
            if (inStream5 < 2) {
               this.pushMessage(decodedString, 98, text, inStream5, inStream6, inStream7);
            } else {
               this.pushMessage(decodedString, 98, text, inStream5, 0, 0);
            }

            this.pktType = -1;
            return true;
         }

         if (this.pktType == 99) {
            this.minimapState = this.inStream.readUnsignedByte();
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 75) {
            int defaultMediaIdOrInStream = this.inStream.readUnsignedShortLittleEndianAdded();
            int inStream8 = this.inStream.readUnsignedShortLittleEndianAdded();
            Widget.widgets[inStream8].defaultMediaType = 2;
            Widget.widgets[inStream8].defaultMediaId = defaultMediaIdOrInStream;
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 114) {
            this.systemUpdateTime = this.inStream.readUnsignedShortLittleEndian() * 30;
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 60) {
            this.localY = this.inStream.readUnsignedByte();
            this.localX = this.inStream.readUnsignedByteNegated();

            while (this.inStream.currentPosition < this.pktSize) {
               int inStream9 = this.inStream.readUnsignedByte();
               this.parseRegionPackets(this.inStream, inStream9);
            }

            this.pktType = -1;
            return true;
         }

         if (this.pktType == 35) {
            int decodedUnsignedByte = this.inStream.readUnsignedByte();
            int cameraShakeRandomAmplitudeOrInStream = this.inStream.readUnsignedByte();
            widgetIndex2 = this.inStream.readUnsignedByte();
            int cameraShakeSineFrequencyOrInStream = this.inStream.readUnsignedByte();
            this.cameraShakeActive[decodedUnsignedByte] = true;
            this.cameraShakeRandomAmplitude[decodedUnsignedByte] = cameraShakeRandomAmplitudeOrInStream;
            this.cameraShakeSineAmplitude[decodedUnsignedByte] = widgetIndex2;
            this.cameraShakeSineFrequency[decodedUnsignedByte] = cameraShakeSineFrequencyOrInStream;
            this.cameraShakePhase[decodedUnsignedByte] = 0;
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 174) {
            int soundOrInStream = this.inStream.readUnsignedShort();
            int soundTypeOrInStream = this.inStream.readUnsignedByte();
            widgetIndex2 = this.inStream.readUnsignedShort();
            if (soundOrInStream >= 2726 && SoundEffect.effects[soundOrInStream] == null) {
               this.onDemandFetcher.provide(6, soundOrInStream);
            }

            if (soundEffectVolume != 0 && soundTypeOrInStream != 0 && this.currentSound < 50) {
               this.sound[this.currentSound] = soundOrInStream;
               this.soundType[this.currentSound] = soundTypeOrInStream;
               this.soundVolume[this.currentSound] = widgetIndex2;
               queuedSoundEffects[this.currentSound] = null;
               this.currentSound++;
            }

            this.pktType = -1;
            return true;
         }

         if (this.pktType == 104) {
            int inStream10 = this.inStream.readUnsignedByteNegated();
            int atPlayerArrayOrInStream = this.inStream.readUnsignedByteAdded();
            String text2 = this.inStream.readString();
            if (inStream10 > 0 && inStream10 <= 5) {
               if (text2.equalsIgnoreCase("null")) {
                  text2 = null;
               }

               this.atPlayerActions[inStream10 - 1] = text2;
               this.atPlayerArray[inStream10 - 1] = atPlayerArrayOrInStream == 0;
            }

            this.pktType = -1;
            return true;
         }

         if (this.pktType == 78) {
            this.destX = 0;
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 253) {
            String text3;
            if ((text3 = this.inStream.readString()).endsWith(":tradereq:")) {
               String text4;
               long encodedName = NameUtils.encodeBase37(text4 = text3.substring(0, text3.indexOf(":")));
               boolean flag = false;

               for (int ignoreIndex = 0; ignoreIndex < this.ignoreCount; ignoreIndex++) {
                  if (this.ignoreListAsLongs[ignoreIndex] == encodedName) {
                     flag = true;
                     break;
                  }
               }

               if (!flag && this.onTutorialIsland == 0) {
                  this.pushMessage("wishes to trade with you.", 4, text4, 0, 0, 0);
               }
            } else if (text3.endsWith(":duelreq:")) {
               String text5;
               long encodeBase372 = NameUtils.encodeBase37(text5 = text3.substring(0, text3.indexOf(":")));
               boolean localFlag = false;

               for (int ignoreIndex2 = 0; ignoreIndex2 < this.ignoreCount; ignoreIndex2++) {
                  if (this.ignoreListAsLongs[ignoreIndex2] == encodeBase372) {
                     localFlag = true;
                     break;
                  }
               }

               if (!localFlag && this.onTutorialIsland == 0) {
                  this.pushMessage("wishes to duel with you.", 8, text5, 0, 0, 0);
               }
            } else if (text3.endsWith(":grpinv:")) {
               String text6;
               long encodeBase373 = NameUtils.encodeBase37(text6 = text3.substring(0, text3.indexOf(":")));
               boolean flag2 = false;

               for (int ignoreIndex3 = 0; ignoreIndex3 < this.ignoreCount; ignoreIndex3++) {
                  if (this.ignoreListAsLongs[ignoreIndex3] == encodeBase373) {
                     flag2 = true;
                     break;
                  }
               }

               if (!flag2 && this.onTutorialIsland == 0) {
                  this.pushMessage("has invited you to join a group.", 255, text6, 0, 0, 0);
               }
            } else if (!text3.endsWith(":chalreq:")) {
               if (!text3.endsWith(":airtiara:")
                  && !text3.endsWith(":watertiara:")
                  && !text3.endsWith(":earthtiara:")
                  && !text3.endsWith(":firetiara:")
                  && !text3.endsWith(":mindtiara:")
                  && !text3.endsWith(":bodytiara:")
                  && !text3.endsWith(":cosmictiara:")
                  && !text3.endsWith(":chaostiara:")
                  && !text3.endsWith(":naturetiara:")
                  && !text3.endsWith(":lawtiara:")
                  && !text3.endsWith(":deathtiara:")
                  && !text3.endsWith(":notiara:")) {
                  if (text3.startsWith("Welcome to RuneScape")) {
                     this.pushMessage("Welcome to RuneScape", 0, "", 0, 0, 0);
                  } else if (!text3.startsWith("Running RS2 Build") && !text3.startsWith("Return of the Wise Old Man")) {
                     this.pushMessage(text3, 0, "", 0, 0, 0);
                  }
               }
            } else {
               String text7;
               long encodeBase374 = NameUtils.encodeBase37(text7 = text3.substring(0, text3.indexOf(":")));
               boolean flag3 = false;

               for (int ignoreIndex4 = 0; ignoreIndex4 < this.ignoreCount; ignoreIndex4++) {
                  if (this.ignoreListAsLongs[ignoreIndex4] == encodeBase374) {
                     flag3 = true;
                     break;
                  }
               }

               if (!flag3 && this.onTutorialIsland == 0) {
                  String text8 = text3.substring(text3.indexOf(":") + 1, text3.length() - 9);
                  this.pushMessage(text8, 8, text7, 0, 0, 0);
               }
            }

            this.pktType = -1;
            return true;
         }

         if (this.pktType == 1) {
            for (int playerIndex2 = 0; playerIndex2 < this.players.length; playerIndex2++) {
               if (this.players[playerIndex2] != null) {
                  this.players[playerIndex2].turnLeftAnimationId = -1;
               }
            }

            for (int npcIndex2 = 0; npcIndex2 < this.npcs.length; npcIndex2++) {
               if (this.npcs[npcIndex2] != null) {
                  this.npcs[npcIndex2].turnLeftAnimationId = -1;
               }
            }

            this.pktType = -1;
            return true;
         }

         if (this.pktType == 50) {
            long friendEncodedNameOrInStream = this.inStream.readLong();
            widgetIndex2 = this.inStream.readUnsignedByte();
            String text9 = NameUtils.formatDisplayName(NameUtils.decodeBase37(friendEncodedNameOrInStream));

            for (int friendEncodedNameIndex = 0; friendEncodedNameIndex < this.friendCount; friendEncodedNameIndex++) {
               if (friendEncodedNameOrInStream == this.friendEncodedNames[friendEncodedNameIndex]) {
                  if (this.friendWorlds[friendEncodedNameIndex] != widgetIndex2) {
                     this.friendWorlds[friendEncodedNameIndex] = widgetIndex2;
                     this.needDrawTabArea = true;
                     if (widgetIndex2 >= 2) {
                        this.pushMessage(text9 + " has logged in.", 5, "", 0, 0, 0);
                     }

                     if (widgetIndex2 <= 1) {
                        this.pushMessage(text9 + " has logged out.", 5, "", 0, 0, 0);
                     }
                  }

                  text9 = null;
                  break;
               }
            }

            if (text9 != null && this.friendCount < 200) {
               this.friendEncodedNames[this.friendCount] = friendEncodedNameOrInStream;
               this.friendNames[this.friendCount] = text9;
               this.friendWorlds[this.friendCount] = widgetIndex2;
               this.friendCount++;
               this.needDrawTabArea = true;
            }

            boolean flag4 = false;

            while (!flag4) {
               flag4 = true;

               for (int friendWorldIndex = 0; friendWorldIndex < this.friendCount - 1; friendWorldIndex++) {
                  if (this.friendWorlds[friendWorldIndex] != nodeID && this.friendWorlds[friendWorldIndex + 1] == nodeID
                     || this.friendWorlds[friendWorldIndex] == 0 && this.friendWorlds[friendWorldIndex + 1] != 0) {
                     int friendWorldOrFriendWorlds = this.friendWorlds[friendWorldIndex];
                     this.friendWorlds[friendWorldIndex] = this.friendWorlds[friendWorldIndex + 1];
                     this.friendWorlds[friendWorldIndex + 1] = friendWorldOrFriendWorlds;
                     String text10 = this.friendNames[friendWorldIndex];
                     this.friendNames[friendWorldIndex] = this.friendNames[friendWorldIndex + 1];
                     this.friendNames[friendWorldIndex + 1] = text10;
                     long friendEncodedNameOrFriendEncodedNames = this.friendEncodedNames[friendWorldIndex];
                     this.friendEncodedNames[friendWorldIndex] = this.friendEncodedNames[friendWorldIndex + 1];
                     this.friendEncodedNames[friendWorldIndex + 1] = friendEncodedNameOrFriendEncodedNames;
                     this.needDrawTabArea = true;
                     flag4 = false;
                  }
               }
            }

            this.pktType = -1;
            return true;
         }

         if (this.pktType == 110) {
            if (this.currentTab == 12) {
               this.needDrawTabArea = true;
            }

            this.energy = this.inStream.readUnsignedByte();
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 254) {
            this.hintIconDrawType = this.inStream.readUnsignedByte();
            if (this.hintIconDrawType == 1) {
               this.hintIconNpcId = this.inStream.readUnsignedShort();
            }

            if (this.hintIconDrawType >= 2 && this.hintIconDrawType <= 6) {
               if (this.hintIconDrawType == 2) {
                  this.hintIconOffsetX = 64;
                  this.hintIconOffsetY = 64;
               }

               if (this.hintIconDrawType == 3) {
                  this.hintIconOffsetX = 0;
                  this.hintIconOffsetY = 64;
               }

               if (this.hintIconDrawType == 4) {
                  this.hintIconOffsetX = 128;
                  this.hintIconOffsetY = 64;
               }

               if (this.hintIconDrawType == 5) {
                  this.hintIconOffsetX = 64;
                  this.hintIconOffsetY = 0;
               }

               if (this.hintIconDrawType == 6) {
                  this.hintIconOffsetX = 64;
                  this.hintIconOffsetY = 128;
               }

               this.hintIconDrawType = 2;
               this.hintIconX = this.inStream.readUnsignedShort();
               this.hintIconY = this.inStream.readUnsignedShort();
               this.hintIconHeight = this.inStream.readUnsignedByte();
            }

            if (this.hintIconDrawType == 10) {
               this.hintIconPlayerId = this.inStream.readUnsignedShort();
            }

            this.pktType = -1;
            return true;
         }

         if (this.pktType == 248) {
            int sourceOpenInterfaceId;
            if ((sourceOpenInterfaceId = this.inStream.readUnsignedShortAdded()) == 5292) {
               this.updateBankTabs();
            }

            if (sourceOpenInterfaceId == 18939) {
               Widget.widgets[18968].message = "N/A";
               Widget.widgets[18969].message = "0";
               Widget.widgets[18970].message = "1 coins";
               Widget.widgets[18971].message = "0 coins";
               Widget.widgets[18963].message = "Choose an item...";
               Widget.widgets[18967].message = "Choose an item from your inventory to sell.";
               Widget.widgets[18983].disabledSprite = null;
            }

            int invOverlayInterfaceIDOrInStream = this.inStream.readUnsignedShort();
            if (this.backDialogID != -1) {
               this.backDialogID = -1;
               this.inputTaken = true;
            }

            if (this.inputDialogState != 0) {
               this.inputDialogState = 0;
               this.inputTaken = true;
            }

            this.openInterfaceId = sourceOpenInterfaceId;
            this.invOverlayInterfaceID = invOverlayInterfaceIDOrInStream;
            this.needDrawTabArea = true;
            this.tabAreaAltered = true;
            this.continuedDialogue = false;
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 79) {
            int inStream11 = this.inStream.readUnsignedShortLittleEndian();
            int scrollPositionOrInStream = this.inStream.readUnsignedShortAdded();
            Widget widget2;
            if ((widget2 = Widget.widgets[inStream11]) != null && widget2.type == 0) {
               if (scrollPositionOrInStream < 0) {
                  scrollPositionOrInStream = 0;
               }

               if (scrollPositionOrInStream > widget2.scrollMax - widget2.height) {
                  scrollPositionOrInStream = widget2.scrollMax - widget2.height;
               }

               widget2.scrollPosition = scrollPositionOrInStream;
            }

            this.pktType = -1;
            return true;
         }

         if (this.pktType == 68) {
            for (int varpIndex = 0; varpIndex < this.varps.length; varpIndex++) {
               if (this.varps[varpIndex] != this.serverVarps[varpIndex]) {
                  this.varps[varpIndex] = this.serverVarps[varpIndex];
                  this.applyVarpSetting(varpIndex);
                  this.needDrawTabArea = true;
               }
            }

            this.pktType = -1;
            return true;
         }

         if (this.pktType == 196) {
            long inStream12 = this.inStream.readLong();
            this.inStream.readInt();
            int inStream13;
            int chatPrivilege = inStream13 = this.inStream.readUnsignedByte();
            int inStream14 = this.inStream.readUnsignedByte();
            int inStream15 = this.inStream.readUnsignedByte();
            boolean flag5 = false;
            if (inStream13 <= 1) {
               for (int ignoreIndex5 = 0; ignoreIndex5 < this.ignoreCount; ignoreIndex5++) {
                  if (this.ignoreListAsLongs[ignoreIndex5] == inStream12) {
                     flag5 = true;
                  }
               }
            }

            if (!flag5 && this.onTutorialIsland == 0) {
               try {
                  String text11 = ChatCodec.decode(this.pktSize - 15, this.inStream);
                  if (inStream13 != 2 && inStream13 != 3) {
                     this.pushMessage(text11, 7, NameUtils.formatDisplayName(NameUtils.decodeBase37(inStream12)), chatPrivilege, inStream14, inStream15);
                  } else {
                     this.pushMessage(text11, 7, NameUtils.formatDisplayName(NameUtils.decodeBase37(inStream12)), 2, 0, 0);
                  }
               } catch (Exception exception) {
                  SignLink.reporterror("cde1");
               }
            }

            this.pktType = -1;
            return true;
         }

         if (this.pktType == 85) {
            this.localY = this.inStream.readUnsignedByteNegated();
            this.localX = this.inStream.readUnsignedByteNegated();
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 24) {
            this.flashingSidebarId = this.inStream.readUnsignedByteSubtracted();
            if (this.flashingSidebarId == this.currentTab) {
               if (this.flashingSidebarId == 3) {
                  this.currentTab = 1;
               } else {
                  this.currentTab = 3;
               }

               this.needDrawTabArea = true;
            }

            this.pktType = -1;
            return true;
         }

         if (this.pktType == 246) {
            int decodedUnsignedShortLittleEndian2 = this.inStream.readUnsignedShortLittleEndian();
            int inStream16 = this.inStream.readUnsignedShort();
            if ((widgetIndex2 = this.inStream.readUnsignedShort()) == 65535) {
               Widget.widgets[decodedUnsignedShortLittleEndian2].defaultMediaType = 0;
               this.pktType = -1;
               return true;
            }

            ItemDefinition itemDefinition = ItemDefinition.lookup(widgetIndex2);
            Widget.widgets[decodedUnsignedShortLittleEndian2].defaultMediaType = 4;
            Widget.widgets[decodedUnsignedShortLittleEndian2].defaultMediaId = widgetIndex2;
            Widget.widgets[decodedUnsignedShortLittleEndian2].modelRotation1 = itemDefinition.xan2d;
            Widget.widgets[decodedUnsignedShortLittleEndian2].modelRotation2 = itemDefinition.yan2d;
            Widget.widgets[decodedUnsignedShortLittleEndian2].modelZoom = itemDefinition.zoom2d * 100 / inStream16;
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 171) {
            boolean hoverOnlyOrInStream = this.inStream.readUnsignedByte() == 1;
            int inStream17 = this.inStream.readUnsignedShort();
            Widget.widgets[inStream17].hoverOnly = hoverOnlyOrInStream;
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 142) {
            int sourceInvOverlayInterfaceID = this.inStream.readUnsignedShortLittleEndian();
            this.resetWidgetAnimation(sourceInvOverlayInterfaceID);
            if (this.backDialogID != -1) {
               this.backDialogID = -1;
               this.inputTaken = true;
            }

            if (this.inputDialogState != 0) {
               this.inputDialogState = 0;
               this.inputTaken = true;
            }

            this.invOverlayInterfaceID = sourceInvOverlayInterfaceID;
            this.needDrawTabArea = true;
            this.tabAreaAltered = true;
            this.openInterfaceId = -1;
            this.fullscreenInterfaceId = -1;
            this.continuedDialogue = false;
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 250) {
            this.yCameraCurve = this.inStream.readUnsignedByte();
            this.myPrivilege = this.inStream.readUnsignedByte();
            this.xCameraCurve = this.inStream.readUnsignedByte();
            boolean localMyPrivilege;
            if (!(localMyPrivilege = this.myPrivilege > 0 || this.yCameraCurve > 0)) {
               localMyPrivilege = this.gameframeSelectionAllowed;
            }

            if (ClientWindow.getInstance() != null) {
               ClientWindow.gameframeMenu.setEnabled(localMyPrivilege);
            }

            Widget.updateSkillLevelActions(this.yCameraCurve);
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 126) {
            String text12 = this.inStream.readString();
            int inStream18;
            if ((inStream18 = this.inStream.readUnsignedShortAdded()) == 19045 || inStream18 == 19054 || inStream18 == 19063 || inStream18 == 19072 || inStream18 == 19081 || inStream18 == 19090) {
               text12 = wrapText(text12, 85, this.smallFont);
            }

            if (inStream18 == 11301) {
               this.castleWarsCatapultAimX =
                     this.parseCastleWarsCatapultAim(text12, this.castleWarsCatapultAimX);
               Widget.updateCastleWarsCatapultAimMarker(
                     this.castleWarsCatapultAimX, this.castleWarsCatapultAimY);
            } else if (inStream18 == 11302) {
               this.castleWarsCatapultAimY =
                     this.parseCastleWarsCatapultAim(text12, this.castleWarsCatapultAimY);
               Widget.updateCastleWarsCatapultAimMarker(
                     this.castleWarsCatapultAimX, this.castleWarsCatapultAimY);
            }

            if (inStream18 == 5383) {
               this.bankTitle = text12;
               this.updateBankSearch();
            } else {
               Widget.widgets[inStream18].message = text12;
            }

            if (Widget.widgets[inStream18].parentId == this.tabInterfaceIds[this.currentTab]) {
               this.needDrawTabArea = true;
            }

            this.pktType = -1;
            return true;
         }

         if (this.pktType == 206) {
            this.publicChatMode = this.inStream.readUnsignedByte();
            this.privateChatMode = this.inStream.readUnsignedByte();
            this.tradeMode = this.inStream.readUnsignedByte();
            this.chatSettingsRedraw = true;
            this.inputTaken = true;
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 240) {
            if (this.currentTab == 12) {
               this.needDrawTabArea = true;
            }

            this.weight = this.inStream.readShort();
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 8) {
            int inStream19 = this.inStream.readUnsignedShortLittleEndianAdded();
            int sourceDefaultMediaId = this.inStream.readUnsignedShort();
            Widget.widgets[inStream19].defaultMediaType = 1;
            Widget.widgets[inStream19].defaultMediaId = sourceDefaultMediaId;
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 122) {
            int inStream20 = this.inStream.readUnsignedShortLittleEndianAdded();
            int inStream21;
            widgetIndex2 = (inStream21 = this.inStream.readUnsignedShortLittleEndianAdded()) >> 10 & 31;
            int scalar9 = inStream21 >> 5 & 31;
            int scalar10 = inStream21 & 31;
            Widget.widgets[inStream20].textColor = (widgetIndex2 << 19) + (scalar9 << 11) + (scalar10 << 3);
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 53) {
            this.needDrawTabArea = true;
            int inStream22 = this.inStream.readUnsignedShort();
            Widget widget3 = Widget.widgets[inStream22];
            widgetIndex2 = this.inStream.readUnsignedShort();
            int bankTabIndex = -1;

            for (int sourceBankTabIndex = 0; sourceBankTabIndex < bankTabs.length; sourceBankTabIndex++) {
               BankTab bankTab = bankTabs[sourceBankTabIndex];
               if (inStream22 == bankTab.getContainerWidgetId()) {
                  bankTabIndex = sourceBankTabIndex;
                  bankTab.itemCount = 0;
               }
            }

            if (bankTabIndex != -1 && bankTabItemIds == null) {
               bankTabItemIds = new int[bankTabs.length][Math.max(widgetIndex2, widget3.inventoryIds.length)];
               this.bankTabItemAmounts = new int[bankTabs.length][Math.max(widgetIndex2, widget3.inventoryAmounts.length)];
            }

            for (int inventoryIdIndex2 = 0; inventoryIdIndex2 < widgetIndex2; inventoryIdIndex2++) {
               int inventoryAmountOrInStream;
               if ((inventoryAmountOrInStream = this.inStream.readUnsignedByte()) == 255) {
                  inventoryAmountOrInStream = this.inStream.readIntInverseMiddleEndian();
               }

               int inventoryIdOrInStream = this.inStream.readUnsignedShortLittleEndianAdded();
               if (bankTabIndex != -1 && inventoryIdOrInStream != 0) {
                  BankTab bankTab2;
                  (bankTab2 = bankTabs[bankTabIndex]).itemCount = inventoryIdIndex2 + 1;
               }

               if (bankTabIndex != -1) {
                  bankTabItemIds[bankTabIndex][inventoryIdIndex2] = inventoryIdOrInStream;
                  this.bankTabItemAmounts[bankTabIndex][inventoryIdIndex2] = inventoryAmountOrInStream;
               } else {
                  widget3.inventoryIds[inventoryIdIndex2] = inventoryIdOrInStream;
                  widget3.inventoryAmounts[inventoryIdIndex2] = inventoryAmountOrInStream;
               }
            }

            for (int inventoryIdIndex3 = widgetIndex2; inventoryIdIndex3 < widget3.inventoryIds.length; inventoryIdIndex3++) {
               if (bankTabIndex != -1) {
                  bankTabItemIds[bankTabIndex][inventoryIdIndex3] = 0;
                  this.bankTabItemAmounts[bankTabIndex][inventoryIdIndex3] = 0;
               } else {
                  widget3.inventoryIds[inventoryIdIndex3] = 0;
                  widget3.inventoryAmounts[inventoryIdIndex3] = 0;
               }
            }

            if (bankTabIndex != -1) {
               this.updateBankSearch();
            }

            this.pktType = -1;
            return true;
         }

         if (this.pktType == 230) {
            int modelZoomOrInStream = this.inStream.readUnsignedShortAdded();
            int decodedUnsignedShort = this.inStream.readUnsignedShort();
            widgetIndex2 = this.inStream.readUnsignedShort();
            int modelRotation2OrInStream = this.inStream.readUnsignedShortLittleEndianAdded();
            Widget.widgets[decodedUnsignedShort].modelRotation1 = widgetIndex2;
            Widget.widgets[decodedUnsignedShort].modelRotation2 = modelRotation2OrInStream;
            Widget.widgets[decodedUnsignedShort].modelZoom = modelZoomOrInStream;
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 221) {
            this.friendServerStatus = this.inStream.readUnsignedByte();
            this.needDrawTabArea = true;
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 177) {
            this.oriented = true;
            this.cameraTargetTileX = this.inStream.readUnsignedByte();
            this.cameraTargetTileY = this.inStream.readUnsignedByte();
            this.cameraTargetHeightOffset = this.inStream.readUnsignedShort();
            this.cameraTargetMoveSpeed = this.inStream.readUnsignedByte();
            this.cameraTargetMoveAcceleration = this.inStream.readUnsignedByte();
            if (this.cameraTargetMoveAcceleration >= 100) {
               int sqrtResult = (this.cameraTargetTileX << 7) + 64;
               int worldY = (this.cameraTargetTileY << 7) + 64;
               widgetIndex2 = this.getTileHeight(this.plane, worldY, sqrtResult) - this.cameraTargetHeightOffset;
               int scalar11 = sqrtResult - this.cameraPositionX;
               int scalar12 = widgetIndex2 - this.cameraPositionZ;
               worldY -= this.xCameraPos;
               sqrtResult = (int)Math.sqrt(scalar11 * scalar11 + worldY * worldY);
               this.zCameraPos = (int)(Math.atan2(scalar12, sqrtResult) * 325.949) & 2047;
               this.yCameraPos = (int)(Math.atan2(scalar11, worldY) * -325.949) & 2047;
               if (this.zCameraPos < 128) {
                  this.zCameraPos = 128;
               }

               if (this.zCameraPos > 383) {
                  this.zCameraPos = 383;
               }
            }

            this.pktType = -1;
            return true;
         }

         if (this.pktType == 249) {
            this.member = this.inStream.readUnsignedByteAdded();
            this.localPlayerIndex = this.inStream.readUnsignedShortLittleEndianAdded();
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 65) {
            this.updateNPCs(this.inStream, this.pktSize);
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 27) {
            this.messagePromptRaised = false;
            this.inputDialogState = 1;
            this.amountOrNameInput = "";
            this.inputTaken = true;
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 187) {
            this.messagePromptRaised = false;
            this.inputDialogState = 2;
            this.amountOrNameInput = "";
            this.inputTaken = true;
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 97) {
            int fullscreenInterfaceIdOrInStream = this.inStream.readUnsignedShort();
            boolean flag6 = true;
            if (fullscreenInterfaceIdOrInStream == 4282) {
               Widget widget4;
               (widget4 = Widget.widgets[4286]).defaultAnimationId = 475;
               if (hdModels || use2007Models) {
                  int animationFrameArchiveId = getAnimationFrameArchiveId(widget4.defaultAnimationId);

                  try {
                     if (AnimationFrame.frameCache.get(animationFrameArchiveId) == null) {
                        this.onDemandFetcher.provide(1, animationFrameArchiveId);
                     }
                  } catch (Exception exception5) {
                  }
               }
            }

            if (fullscreenInterfaceIdOrInStream == 18890) {
               Widget.widgets[18919].message = "N/A";
               Widget.widgets[18920].message = "0";
               Widget.widgets[18921].message = "1 coins";
               Widget.widgets[18922].message = "0 coins";
               Widget.widgets[18914].message = "Choose an item to exchange";
               Widget.widgets[18918].message = "Click the icon to the left to search for items.";
               Widget.widgets[18938].disabledSprite = null;
               Widget.widgets[18933].hoverOnly = false;
               Widget.widgets[18936].hoverOnly = true;
               this.amountOrNameInput = "";
               this.clanChatMode = 0;
               itemSearchSpawnMode = false;
               this.inputDialogState = 3;
               flag6 = false;
            }

            this.resetWidgetAnimation(fullscreenInterfaceIdOrInStream);
            if (this.invOverlayInterfaceID != -1) {
               this.invOverlayInterfaceID = -1;
               this.needDrawTabArea = true;
               this.tabAreaAltered = true;
            }

            if (this.backDialogID != -1) {
               this.backDialogID = -1;
               this.inputTaken = true;
            }

            if (this.inputDialogState != 0 && flag6) {
               this.inputDialogState = 0;
               this.inputTaken = true;
            }

            if (this.fullscreenInterfaceId == -1) {
               if (screenMode != 0 && (fullscreenInterfaceIdOrInStream == 18178 || fullscreenInterfaceIdOrInStream == 18220 || fullscreenInterfaceIdOrInStream == 6946 || fullscreenInterfaceIdOrInStream == 8016)) {
                  this.fullscreenInterfaceBackdropVisible = true;
                  this.fullscreenInterfaceCoversViewport = true;
               }

               if (fullscreenInterfaceIdOrInStream == 18788) {
                  this.fullscreenInterfaceId = fullscreenInterfaceIdOrInStream;
               } else {
                  this.openInterfaceId = fullscreenInterfaceIdOrInStream;
               }
            }

            this.continuedDialogue = false;
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 69) {
            int openInterfaceIdOrInStream2 = this.inStream.readUnsignedShort();
            int sourceFullscreenInterfaceId = this.inStream.readUnsignedShort();
            this.resetWidgetAnimation(openInterfaceIdOrInStream2);
            this.openInterfaceId = openInterfaceIdOrInStream2;
            this.fullscreenInterfaceId = sourceFullscreenInterfaceId;
            this.continuedDialogue = false;
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 218) {
            int dialogIDOrInStream = this.inStream.readShortLittleEndianAdded();
            this.dialogID = dialogIDOrInStream;
            this.inputTaken = true;
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 87) {
            int serverVarpIndex = this.inStream.readUnsignedShortLittleEndian();
            int serverVarpOrInStream = this.inStream.readIntMiddleEndian();
             if (this.shouldIgnoreStaleCombatStyleVarp(serverVarpIndex, serverVarpOrInStream)) {
                this.pktType = -1;
                return true;
             }
            this.serverVarps[serverVarpIndex] = serverVarpOrInStream;
            if (this.varps[serverVarpIndex] != serverVarpOrInStream) {
               this.varps[serverVarpIndex] = serverVarpOrInStream;
               this.applyVarpSetting(serverVarpIndex);
               this.needDrawTabArea = true;
               if (this.dialogID != -1) {
                  this.inputTaken = true;
               }
            }

            this.pktType = -1;
            return true;
         }

         if (this.pktType == 36) {
            int definitionIndex = this.inStream.readUnsignedShortLittleEndian();
            byte decodedByte = this.inStream.readByte();
             if (this.shouldIgnoreStaleCombatStyleVarp(definitionIndex, decodedByte)) {
                this.pktType = -1;
                return true;
             }
            this.serverVarps[definitionIndex] = decodedByte;
            if (this.varps[definitionIndex] != decodedByte) {
               this.varps[definitionIndex] = decodedByte;
               this.applyVarpSetting(definitionIndex);
               this.needDrawTabArea = true;
               if (this.dialogID != -1) {
                  this.inputTaken = true;
               }
            }

            this.pktType = -1;
            return true;
         }

         if (this.pktType == 61) {
            this.multicombat = this.inStream.readUnsignedByte();
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 200) {
            int inStream23 = this.inStream.readUnsignedShort();
            int defaultAnimationIdOrInStream = this.inStream.readShort();
            Widget widget5;
            (widget5 = Widget.widgets[inStream23]).defaultAnimationId = defaultAnimationIdOrInStream;
            if (hdModels) {
               widget5.modelZoom = 1600;
            } else {
               widget5.modelZoom = 796;
            }

            if (defaultAnimationIdOrInStream == -1) {
               widget5.animationFrame = 0;
               widget5.animationFrameCycle = 0;
            }

            this.pktType = -1;
            return true;
         }

         if (this.pktType == 219) {
            if (this.invOverlayInterfaceID != -1) {
               this.invOverlayInterfaceID = -1;
               this.needDrawTabArea = true;
               this.tabAreaAltered = true;
            }

            if (this.backDialogID != -1) {
               this.backDialogID = -1;
               this.inputTaken = true;
            }

            if (this.inputDialogState != 0) {
               this.inputDialogState = 0;
               this.inputTaken = true;
            }

            this.openInterfaceId = -1;
            this.fullscreenInterfaceId = -1;
            this.fullscreenInterfaceBackdropVisible = false;
            this.fullscreenInterfaceCoversViewport = false;
            this.continuedDialogue = false;
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 34) {
            this.needDrawTabArea = true;
            int inStream24 = this.inStream.readUnsignedShort();
            Widget widget6 = Widget.widgets[inStream24];

            while (this.inStream.currentPosition < this.pktSize) {
               widgetIndex2 = this.inStream.readUnsignedSmart();
               int decodedUnsignedShort2 = this.inStream.readUnsignedShort();
               int localInventoryAmount;
               if ((localInventoryAmount = this.inStream.readUnsignedByte()) == 255) {
                  localInventoryAmount = this.inStream.readInt();
               }

               if (widgetIndex2 >= 0 && widgetIndex2 < widget6.inventoryIds.length) {
                  widget6.inventoryIds[widgetIndex2] = decodedUnsignedShort2;
                  widget6.inventoryAmounts[widgetIndex2] = localInventoryAmount;
               }
            }

            this.pktType = -1;
            return true;
         }

         if (this.pktType == 16) {
            this.needDrawTabArea = true;
            int inStream25 = this.inStream.readUnsignedShort();
            int inStream26 = this.inStream.readUnsignedShort();
            Widget widget7 = Widget.widgets[inStream25];
            Sprite sprite = ItemDefinition.getSprite(inStream26, 1, 0);
            widget7.disabledSprite = sprite;
            ItemDefinition itemDefinition2;
            String text13;
            if ((itemDefinition2 = ItemDefinition.lookup(inStream26)).description != null) {
               text13 = new String(itemDefinition2.description);
            } else {
               text13 = "It's a " + itemDefinition2.name + ".";
            }

            text13 = wrapText(text13, 280, this.smallFont);
            if (inStream25 == 18983) {
               Widget.widgets[18963].message = itemDefinition2.name;
               Widget.widgets[18967].message = text13;
            }

            if (inStream25 == 19008) {
               Widget.widgets[18993].message = itemDefinition2.name;
               Widget.widgets[18996].message = text13;
            }

            if (inStream25 == 18938) {
               Widget.widgets[18914].message = itemDefinition2.name;
               Widget.widgets[18918].message = text13;
               Widget.widgets[18933].hoverOnly = true;
               Widget.widgets[18936].hoverOnly = false;
            }

            this.pktType = -1;
            return true;
         }

         if (this.pktType == 18) {
            this.needDrawTabArea = true;
            int widthOrInStream = this.inStream.readUnsignedShort();
            Widget widget8 = Widget.widgets[widthOrInStream];
            int inStream27;
            if ((inStream27 = this.inStream.readUnsignedByte()) == 250) {
               widget8.textColor = packRgb(138, 0, 16);
               inStream27 = 100;
            } else if (inStream27 < 100) {
               widget8.textColor = packRgb(198, 139, 1);
            } else {
               widget8.textColor = packRgb(0, 95, 0);
            }

            double calculation;
            double calculation2 = (calculation = inStream27) / 100.0;
            widthOrInStream = (int)(widget8.baseWidth * calculation2);
            widget8.width = widthOrInStream;
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 105
            || this.pktType == 84
            || this.pktType == 147
            || this.pktType == 215
            || this.pktType == 4
            || this.pktType == 117
            || this.pktType == 156
            || this.pktType == 44
            || this.pktType == 160
            || this.pktType == 101
            || this.pktType == 151) {
            this.parseRegionPackets(this.inStream, this.pktType);
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 15) {
            int inStream28 = this.inStream.readUnsignedByte();
            int poisonedOrInStream = this.inStream.readUnsignedByte();
            if (inStream28 == 0) {
               this.poisoned = poisonedOrInStream == 1;
            } else if (inStream28 == 1) {
               this.runEnabled = poisonedOrInStream == 1;
            }

            this.pktType = -1;
            return true;
         }

         if (this.pktType == 106) {
            this.currentTab = this.inStream.readUnsignedByteNegated();
            this.needDrawTabArea = true;
            this.tabAreaAltered = true;
            this.pktType = -1;
            return true;
         }

         if (this.pktType == 164) {
            int backDialogIDOrInStream = this.inStream.readUnsignedShortLittleEndian();
            this.resetWidgetAnimation(backDialogIDOrInStream);
            if (this.invOverlayInterfaceID != -1) {
               this.invOverlayInterfaceID = -1;
               this.needDrawTabArea = true;
               this.tabAreaAltered = true;
            }

            this.backDialogID = backDialogIDOrInStream;
            if (this.backDialogID == 4282) {
               Widget widget9;
               (widget9 = Widget.widgets[4286]).defaultAnimationId = 475;
               if (hdModels || use2007Models) {
                  int getAnimationFrameArchiveId2 = getAnimationFrameArchiveId(widget9.defaultAnimationId);

                  try {
                     if (AnimationFrame.frameCache.get(getAnimationFrameArchiveId2) == null) {
                        this.onDemandFetcher.provide(1, getAnimationFrameArchiveId2);
                     }
                  } catch (Exception exception2) {
                  }
               }
            }

            this.inputTaken = true;
            this.openInterfaceId = -1;
            this.fullscreenInterfaceId = -1;
            this.fullscreenInterfaceBackdropVisible = false;
            this.fullscreenInterfaceCoversViewport = false;
            this.continuedDialogue = false;
            this.pktType = -1;
            return true;
         }

         SignLink.reporterror("T1 - " + this.pktType + "," + this.pktSize + " - " + this.prevPktType + "," + this.prevPktType2);
         this.resetLogout();
      } catch (IOException exception3) {
         this.dropClient();
      } catch (Exception exception4) {
         exception4.printStackTrace();
         String text14 = "T2 - "
            + this.pktType
            + ","
            + this.prevPktType
            + ","
            + this.prevPktType2
            + " - "
            + this.pktSize
            + ","
            + (this.baseX + localPlayer.pathX[0])
            + ","
            + (this.baseY + localPlayer.pathY[0])
            + " - ";

         for (int bufferIndex = 0; bufferIndex < this.pktSize && bufferIndex < 50; bufferIndex++) {
            text14 = text14 + this.inStream.buffer[bufferIndex] + ",";
         }

         SignLink.reporterror(text14);
         this.resetLogout();
      }

      return true;
   }
   private void updateParticles() {
      if (particleRenderingEnabled) {
         Iterator iterator = this.particles.iterator();

         while (iterator.hasNext()) {
            Particle particle;
            if ((particle = (Particle)iterator.next()) != null) {
               particle.update();
               if (particle.isDead()) {
                  this.deadParticles.add(particle);
               } else {
                  int particleXOrYawCosine = particle.getPosition().getX();
                  int particleX = particle.getPosition().getY();
                  int particleScalar = particle.getPosition().getZ();
                  int worldY = particleScalar;
                  particleScalar = particleX;
                  particleX = particleXOrYawCosine;
                  Client client = this;
                  int[] values;
                  if (particleX >= 128 && worldY >= 128 && particleX <= 13056 && worldY <= 13056) {
                     particleScalar = client.getTileHeight(client.plane, worldY, particleX) - particleScalar;
                     particleX -= client.cameraPositionX;
                     particleScalar -= client.cameraPositionZ;
                     worldY -= client.xCameraPos;
                     int sINEEntry = Model.SINE[client.zCameraPos];
                     int cOSINEEntry = Model.COSINE[client.zCameraPos];
                     int SINE2 = Model.SINE[client.yCameraPos];
                     particleXOrYawCosine = Model.COSINE[client.yCameraPos];
                     int sourceParticleX = worldY * SINE2 + particleX * particleXOrYawCosine >> 16;
                     worldY = worldY * particleXOrYawCosine - particleX * SINE2 >> 16;
                     particleX = sourceParticleX;
                     sourceParticleX = particleScalar * cOSINEEntry - worldY * sINEEntry >> 16;
                     int scalar;
                     values = (scalar = particleScalar * sINEEntry + worldY * cOSINEEntry >> 16) >= 50 && scalar <= 3500
                        ? new int[]{
                           Rasterizer3D.viewportCenterX + (particleX << getProjectionScaleShift()) / scalar,
                           Rasterizer3D.viewportCenterY + (sourceParticleX << getProjectionScaleShift()) / scalar,
                           scalar,
                           Rasterizer3D.viewportCenterX + (particleX / 2 << getProjectionScaleShift()) / scalar,
                           Rasterizer3D.viewportCenterY + (sourceParticleX / 2 << getProjectionScaleShift()) / scalar,
                           Rasterizer3D.viewportCenterX + (particleX / 2 << getProjectionScaleShift()) / scalar,
                           Rasterizer3D.viewportCenterY + (sourceParticleX / 2 << getProjectionScaleShift()) / scalar
                        }
                        : new int[7];
                  } else {
                     values = new int[7];
                  }

                  int[] integerBuffer = values;
                  float size = particle.getSize();
                  particleScalar = (int)(particle.getAlpha() * 255.0F);
                  worldY = (int)(4.0F * particle.getSize());
                  int scalar2 = 256 - particleScalar;
                  int scalar3 = (particle.getColor() >> 16 & 0xFF) * particleScalar;
                  int scalar4 = (particle.getColor() >> 8 & 0xFF) * particleScalar;
                  int scalar5 = (particle.getColor() & 0xFF) * particleScalar;
                  if ((particleScalar = integerBuffer[1] - worldY) < 0) {
                     particleScalar = 0;
                  }

                  int loopIndex;
                  if ((loopIndex = integerBuffer[1] + worldY) >= Rasterizer2D.height) {
                     loopIndex = Rasterizer2D.height - 1;
                  }

                  for (int loopIndex2 = particleScalar; loopIndex2 <= loopIndex; loopIndex2++) {
                     int sqrtResult = loopIndex2 - integerBuffer[1];
                     sqrtResult = (int)Math.sqrt(worldY * worldY - sqrtResult * sqrtResult);
                     int position;
                     if ((position = integerBuffer[0] - sqrtResult) < 0) {
                        position = 0;
                     }

                     if ((sqrtResult = integerBuffer[0] + sqrtResult) >= Rasterizer2D.width) {
                        sqrtResult = Rasterizer2D.width - 1;
                     }

                     int depthBufferIndex = position + loopIndex2 * Rasterizer2D.width;

                     try {
                        if (Rasterizer3D.depthBuffer != null && (Rasterizer3D.depthBuffer[depthBufferIndex] >= integerBuffer[2] - size - 15.0F || Rasterizer3D.depthBuffer[depthBufferIndex++] >= integerBuffer[2] + size + 15.0F)) {
                           for (int loopIndex3 = position; loopIndex3 <= sqrtResult; loopIndex3++) {
                              int pixel = (this.gameScreenImageProducer.pixels[depthBufferIndex] >> 16 & 0xFF) * scalar2;
                              int scalar6 = (this.gameScreenImageProducer.pixels[depthBufferIndex] >> 8 & 0xFF) * scalar2;
                              int scalar7 = (this.gameScreenImageProducer.pixels[depthBufferIndex] & 0xFF) * scalar2;
                              pixel = (scalar3 + pixel >> 8 << 16) + (scalar4 + scalar6 >> 8 << 8) + (scalar5 + scalar7 >> 8);
                              this.gameScreenImageProducer.pixels[depthBufferIndex++] = pixel;
                           }
                        }
                     } catch (Exception exception) {
                     }
                  }
               }
            }
         }
      } else {
         Iterator iterator2 = this.particles.iterator();

         while (iterator2.hasNext()) {
            Particle particle2;
            if ((particle2 = (Particle)iterator2.next()) != null) {
               particle2.update();
               if (particle2.isDead()) {
                  this.deadParticles.add(particle2);
               }
            }
         }

         this.particles.removeAll(this.deadParticles);
         this.deadParticles.clear();
      }

      this.particles.removeAll(this.deadParticles);
      this.deadParticles.clear();
   }
   private void updateFog() {
      if (fogEnabled) {
         double localSqrt = Math.sqrt(Math.pow(this.cameraPositionZ, 2.0));
         this.fogRenderer.setDistanceOffset((float)localSqrt);
         this.fogRenderer.renderFog(1430, 2100, 3);
      }
   }
   private void addParticle(Particle particle) {
      this.particles.add(particle);
   }
   private void closeTopInterfaces() {
      this.outgoingBuffer.writeOpcode(130);
      if (this.inputDialogState == 3) {
         this.amountOrNameInput = "";
         this.clanChatMode = 0;
         this.inputDialogState = 0;
         this.inputTaken = true;
      }

      if (this.openInterfaceId == 5292 && this.inputDialogState == 2) {
         this.inputDialogState = 0;
         this.inputTaken = true;
      }

      if (this.invOverlayInterfaceID != -1) {
         this.invOverlayInterfaceID = -1;
         this.needDrawTabArea = true;
         this.continuedDialogue = false;
         this.tabAreaAltered = true;
      }

      if (this.backDialogID != -1) {
         this.backDialogID = -1;
         this.inputTaken = true;
         this.continuedDialogue = false;
      }

      this.openInterfaceId = -1;
      this.fullscreenInterfaceBackdropVisible = false;
      this.fullscreenInterfaceCoversViewport = false;
      this.suppressNextMinimapClick = true;
      this.fullscreenInterfaceId = -1;
   }

   public Client() {
      serverAddress = "127.0.0.1";
      this.hoveredChatMode = -1;
      tiara = 0;
      chatViewMode = 0;
      this.particles = new ArrayList(10000);
      this.deadParticles = new ArrayList();
      this.pathDistances = new int[104][104];
      this.friendWorlds = new int[200];
      this.groundItems = new NodeDeque[4][104][104];
      this.flameThreadRunning = false;
      this.chatBuffer = new Buffer(new byte[5000]);
      this.npcs = new Npc[16384];
      this.npcIndices = new int[16384];
      this.removedEntityIndices = new int[1000];
      this.loginBuffer = Buffer.acquire();
      this.openInterfaceId = -1;
      this.currentExp = new int[25];
      this.archiveRetryToggle = false;
      this.cameraShakeRandomAmplitude = new int[5];
      this.cameraShakeActive = new boolean[5];
      this.drawFlames = false;
      this.reportAbuseInput = "";
      this.localPlayerIndex = -1;
      this.menuOpen = false;
      this.inputString = "";
      this.maxPlayers = 2048;
      this.myPlayerIndex = 2047;
      this.players = new Player[2048];
      this.playerIndices = new int[2048];
      this.entityUpdateIndices = new int[2048];
      this.playerAppearanceBuffers = new Buffer[2048];
      this.cameraRotationVelocity = 1;
      this.pathDirections = new int[104][104];
      this.unusedColor7759444 = 7759444;
      this.animatedTextureScratch = new byte[16384];
      this.currentStats = new int[25];
      this.ignoreListAsLongs = new long[100];
      this.loadingError = false;
      this.unusedColor3353893 = 3353893;
      this.cameraShakeSineFrequency = new int[5];
      this.tileCycleMarkers = new int[104][104];
      this.chatTypes = new int[100];
      this.chatNames = new String[100];
      this.chatMessages = new String[100];
      this.chatPrivileges = new int[100];
      this.chatDonatorStatuses = new int[100];
      this.chatAccountModes = new int[100];
      this.sideIcons = new IndexedSprite[13];
      mapBackSprites = new IndexedSprite[2];
      this.focusReported = true;
      this.friendEncodedNames = new long[200];
      this.currentSong = -1;
      this.drawingFlames = false;
      this.spriteDrawX = -1;
      this.spriteDrawY = -1;
      compassMaskLineOffsets = new int[33];
      this.flameLineOffsets = new int[256];
      this.cacheStores = new CacheStore[cacheStoreCount];
      this.varps = new int[2000];
      this.scrollbarDragging = false;
      this.overheadTextCapacity = 50;
      this.overheadTextX = new int[50];
      this.overheadTextY = new int[50];
      this.overheadTextHeight = new int[50];
      this.overheadTextHalfWidth = new int[50];
      this.textColourEffect = new int[50];
      this.overheadTextEffects = new int[50];
      this.overheadTextCycles = new int[50];
      this.overheadTexts = new String[50];
      this.lastRenderedPlane = -1;
      this.hitMarks = new Sprite[20];
      this.characterDesignColours = new int[5];
      this.unusedCharacterFlag = false;
      this.unusedColor2301979 = 2301979;
      this.amountOrNameInput = "";
      this.projectiles = new NodeDeque();
      this.cameraPacketPending = false;
      this.openWalkableInterface = -1;
      this.cameraShakePhase = new int[5];
      this.characterDesignNeedsRebuild = false;
      this.mapFunctions = new Sprite[100];
      this.dialogID = -1;
      this.fullscreenInterfaceId = -1;
      this.maxStats = new int[25];
      this.serverVarps = new int[2000];
      this.maleCharacter = true;
      minimapMaskLineOffsets = new int[151];
      this.flashingSidebarId = -1;
      this.incompleteAnimables = new NodeDeque();
      compassMaskLineWidths = new int[33];
      this.autocastWidget = new Widget();
      this.mapSceneSprites = new IndexedSprite[100];
      this.barFillColor = 5063219;
      this.characterDesignKitIds = new int[7];
      this.minimapHintX = new int[1000];
      this.minimapHintY = new int[1000];
      this.validLocalMap = false;
      this.friendNames = new String[200];
      this.inStream = Buffer.acquire();
      this.archiveCrcs = new int[9];
      this.menuParam0 = new int[500];
      this.menuParam1 = new int[500];
      this.menuActionIds = new int[500];
      this.menuParam2 = new int[500];
      this.headIcons = new Sprite[20];
      this.headIconsHint = new Sprite[20];
      miscInterfaceSprites = new Sprite[20];
      this.skullIcons = new Sprite[20];
      this.tabAreaAltered = false;
      this.promptMessage = "";
      this.atPlayerActions = new String[5];
      this.atPlayerArray = new boolean[5];
      this.instanceChunkTemplates = new int[4][13][13];
      this.cameraYVelocity = 2;
      this.minimapHint = new Sprite[1000];
      this.inPlayerOwnedHouse = false;
      this.continuedDialogue = false;
      this.crosses = new Sprite[8];
      this.skillIconSprites = new Sprite[21];
      this.needDrawTabArea = false;
      loggedIn = false;
      this.canMute = false;
      this.constructedViewport = false;
      this.oriented = false;
      this.minimapRotationVelocity = 1;
      username = "";
      password = "";
      this.genericLoadingError = false;
      this.reportAbuseInterfaceID = -1;
      this.spawns = new NodeDeque();
      this.cameraPitch = 128;
      this.invOverlayInterfaceID = -1;
      this.outgoingBuffer = Buffer.acquire();
      this.menuActionNames = new String[500];
      this.cameraShakeSineAmplitude = new int[5];
      this.sound = new int[50];
      this.minimapZoomVelocity = 2;
      this.chatContentHeight = 78;
      this.promptInput = "";
      this.moderatorIcons = new IndexedSprite[3];
      this.gameModeIcons = new IndexedSprite[4];
      this.currentTab = 3;
      this.inputTaken = false;
      minimapMaskLineWidths = new int[151];
      this.collisionMaps = new CollisionMap[4];
      this.chatSettingsRedraw = false;
      this.soundType = new int[50];
      this.widgetDragThresholdExceeded = false;
      this.soundVolume = new int[50];
      this.rsAlreadyLoaded = false;
      this.welcomeScreenRaised = false;
      this.messagePromptRaised = false;
      this.loginMessage1 = "";
      this.loginMessage2 = "";
      this.backDialogID = -1;
      this.fullscreenInterfaceId = -1;
      this.cameraJitterXVelocity = 2;
      this.bigX = new int[4000];
      this.bigY = new int[4000];
   }
}
