package client;
abstract class PcmStreamMixerListener extends Node {
   int remainingSamples;
   abstract int update();
}
